package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.dto.ImportResponse;
import com.caprae.leadintelligence.entity.*;
import com.caprae.leadintelligence.exception.ImportProcessingException;
import com.caprae.leadintelligence.repository.CompanyRepository;
import com.caprae.leadintelligence.repository.ContactRepository;
import com.caprae.leadintelligence.repository.LeadRepository;
import com.caprae.leadintelligence.scoring.LeadScoreResult;
import com.caprae.leadintelligence.scoring.ScoringEngine;
import com.caprae.leadintelligence.util.CSVParserUtil;
import com.caprae.leadintelligence.util.NormalizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadImportService {

    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final LeadRepository leadRepository;
    private final ValidationService validationService;
    private final DeduplicationService deduplicationService;
    private final ScoringEngine scoringEngine;

    @Transactional
    @CacheEvict(value = "dashboardSummary", allEntries = true)
    public ImportResponse importCsv(MultipartFile file) {
        long startTime = System.currentTimeMillis();

        if (file == null || file.isEmpty()) {
            throw new ImportProcessingException("Uploaded CSV file is empty");
        }

        List<CSVParserUtil.RawLeadRecord> rawRecords;
        try (InputStream is = file.getInputStream()) {
            rawRecords = CSVParserUtil.parseCsv(is);
        } catch (Exception e) {
            log.error("Failed to parse CSV file", e);
            throw new ImportProcessingException("Failed to parse CSV file: " + e.getMessage(), e);
        }

        int totalRows = rawRecords.size();
        int validRows = 0;
        int newOpportunities = 0;
        int updatedRecords = 0;
        int duplicatesRemoved = 0;
        int invalidRecords = 0;
        int highPriority = 0;
        int mediumPriority = 0;
        int lowPriority = 0;

        // DB Index for pre-existing companies
        Map<String, Company> dbIndex = new HashMap<>();
        List<Company> existingCompanies = companyRepository.findAll();
        for (Company c : existingCompanies) {
            deduplicationService.registerInBatch(c, dbIndex);
        }

        // Batch Index for intra-file deduplication
        Map<String, Company> batchIndex = new HashMap<>();

        Map<String, Contact> existingContactsByEmail = new HashMap<>();
        List<Contact> existingContacts = contactRepository.findAll();
        for (Contact ct : existingContacts) {
            if (ct.getEmail() != null && !ct.getEmail().isBlank()) {
                existingContactsByEmail.put(ct.getEmail().trim().toLowerCase(Locale.ROOT), ct);
            }
        }

        Map<Long, Lead> existingLeadsByCompanyId = new HashMap<>();
        List<Lead> existingLeads = leadRepository.findAll();
        for (Lead l : existingLeads) {
            if (l.getCompany() != null && l.getCompany().getId() != null) {
                existingLeadsByCompanyId.put(l.getCompany().getId(), l);
            }
        }

        Map<Company, Lead> batchLeadsByCompany = new HashMap<>();
        List<Company> companiesToSave = new ArrayList<>();
        List<Contact> contactsToSave = new ArrayList<>();
        List<Lead> leadsToSave = new ArrayList<>();

        for (CSVParserUtil.RawLeadRecord raw : rawRecords) {
            // 1. Validate required company name
            if (!validationService.isValidCompanyName(raw.companyName)) {
                invalidRecords++;
                continue;
            }
            validRows++;

            // 2. Normalize firmographic fields
            String domain = NormalizationUtil.normalizeDomain(raw.domain.isBlank() ? raw.website : raw.domain);
            String normName = NormalizationUtil.normalizeCompanyName(raw.companyName);
            String normLoc = NormalizationUtil.normalizeLocation(raw.location);
            BigDecimal revenue = validationService.parseRevenue(raw.estimatedRevenue);
            Integer employees = validationService.parseEmployeeCount(raw.employeeCount);

            Company candidateCompany = Company.builder()
                .companyName(raw.companyName.trim())
                .domain(domain)
                .normalizedName(normName)
                .industry(raw.industry != null ? raw.industry.trim() : "")
                .location(raw.location != null ? raw.location.trim() : "")
                .city(raw.city != null ? raw.city.trim() : "")
                .state(raw.state != null ? raw.state.trim() : "")
                .country(raw.country != null ? raw.country.trim() : "")
                .employeeCount(employees)
                .estimatedRevenue(revenue)
                .website(raw.website != null ? raw.website.trim() : "")
                .description(raw.description != null ? raw.description.trim() : "")
                .build();

            // 3. Deduplication check: first check intra-file batch, then DB index
            Optional<Company> intraBatchOpt = deduplicationService.findDuplicate(candidateCompany, batchIndex);
            Company savedCompany;

            if (intraBatchOpt.isPresent()) {
                // Intra-file duplicate! Skip duplicate row from double-scoring
                savedCompany = intraBatchOpt.get();
                duplicatesRemoved++;

                if (savedCompany.getEstimatedRevenue() == null && revenue != null) {
                    savedCompany.setEstimatedRevenue(revenue);
                }
                if (savedCompany.getEmployeeCount() == null && employees != null) {
                    savedCompany.setEmployeeCount(employees);
                }
                if ((savedCompany.getIndustry() == null || savedCompany.getIndustry().isBlank()) && !candidateCompany.getIndustry().isBlank()) {
                    savedCompany.setIndustry(candidateCompany.getIndustry());
                }
                continue;
            }

            // Unique record in this CSV batch: check pre-existing DB index
            Optional<Company> dbOpt = deduplicationService.findDuplicate(candidateCompany, dbIndex);
            if (dbOpt.isPresent()) {
                // Pre-existing DB company matched and updated
                savedCompany = dbOpt.get();
                updatedRecords++;
                deduplicationService.registerInBatch(savedCompany, batchIndex);
            } else {
                // Brand new opportunity
                savedCompany = candidateCompany;
                newOpportunities++;
                deduplicationService.registerInBatch(savedCompany, batchIndex);
                deduplicationService.registerInBatch(savedCompany, dbIndex);
            }

            if (savedCompany.getId() == null && !companiesToSave.contains(savedCompany)) {
                companiesToSave.add(savedCompany);
            }

            // 4. Contact processing
            Contact savedContact = null;
            if ((raw.email != null && !raw.email.isBlank()) || (raw.firstName != null && !raw.firstName.isBlank())) {
                boolean emailValid = validationService.isValidEmail(raw.email);
                String cleanEmail = raw.email != null ? raw.email.trim().toLowerCase(Locale.ROOT) : "";

                if (emailValid && !cleanEmail.isBlank() && existingContactsByEmail.containsKey(cleanEmail)) {
                    savedContact = existingContactsByEmail.get(cleanEmail);
                } else {
                    savedContact = Contact.builder()
                        .company(savedCompany)
                        .firstName(raw.firstName != null ? raw.firstName.trim() : "")
                        .lastName(raw.lastName != null ? raw.lastName.trim() : "")
                        .jobTitle(raw.jobTitle != null ? raw.jobTitle.trim() : "")
                        .email(raw.email != null ? raw.email.trim() : "")
                        .phone(raw.phone != null ? raw.phone.trim() : "")
                        .linkedinUrl(raw.linkedinUrl != null ? raw.linkedinUrl.trim() : "")
                        .emailVerified(emailValid)
                        .phoneVerified(raw.phone != null && !raw.phone.isBlank())
                        .build();

                    if (emailValid && !cleanEmail.isBlank()) {
                        existingContactsByEmail.put(cleanEmail, savedContact);
                    }
                    contactsToSave.add(savedContact);
                }
            }

            // 5. Create or Update Lead
            Lead lead = null;
            if (savedCompany.getId() != null) {
                lead = existingLeadsByCompanyId.get(savedCompany.getId());
            }
            if (lead == null) {
                lead = batchLeadsByCompany.get(savedCompany);
            }
            if (lead == null) {
                lead = Lead.builder().company(savedCompany).build();
                batchLeadsByCompany.put(savedCompany, lead);
            }

            if (savedContact != null) {
                lead.setContact(savedContact);
            }

            // 6. Score lead deterministically
            LeadScoreResult scoreResult = scoringEngine.score(savedCompany, lead.getContact());
            scoringEngine.applyScoreToLead(lead, scoreResult);

            if (!leadsToSave.contains(lead)) {
                leadsToSave.add(lead);
            }

            if (scoreResult.getPriority() == LeadPriority.HIGH) highPriority++;
            else if (scoreResult.getPriority() == LeadPriority.MEDIUM) mediumPriority++;
            else lowPriority++;
        }

        // Bulk persist
        if (!companiesToSave.isEmpty()) {
            companyRepository.saveAll(companiesToSave);
        }
        if (!contactsToSave.isEmpty()) {
            contactRepository.saveAll(contactsToSave);
        }
        if (!leadsToSave.isEmpty()) {
            leadRepository.saveAll(leadsToSave);
        }

        long processingTimeMs = System.currentTimeMillis() - startTime;
        log.info("CSV Import completed: total={}, valid={}, dupes={}, invalid={}, time={}ms",
            totalRows, validRows, duplicatesRemoved, invalidRecords, processingTimeMs);

        int uniqueOpportunities = newOpportunities + updatedRecords;
        return ImportResponse.builder()
            .totalRows(totalRows)
            .validRows(validRows)
            .uniqueOpportunities(uniqueOpportunities)
            .newOpportunities(newOpportunities)
            .updatedRecords(updatedRecords)
            .duplicatesRemoved(duplicatesRemoved)
            .invalidRecords(invalidRecords)
            .highPriority(highPriority)
            .mediumPriority(mediumPriority)
            .lowPriority(lowPriority)
            .processingTimeMs(processingTimeMs)
            .build();
    }
}

