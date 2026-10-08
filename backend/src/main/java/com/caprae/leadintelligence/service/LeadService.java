package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.dto.LeadFilterRequest;
import com.caprae.leadintelligence.dto.LeadResponse;
import com.caprae.leadintelligence.entity.*;
import com.caprae.leadintelligence.exception.ResourceNotFoundException;
import com.caprae.leadintelligence.repository.CompanyRepository;
import com.caprae.leadintelligence.repository.ContactRepository;
import com.caprae.leadintelligence.repository.LeadRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @CacheEvict(value = "dashboardSummary", allEntries = true)
    public void clearAllData() {
        leadRepository.deleteAllInBatch();
        contactRepository.deleteAllInBatch();
        companyRepository.deleteAllInBatch();
        log.info("Cleared all leads, contacts, and companies from database");
    }

    @Transactional(readOnly = true)
    public Page<LeadResponse> getLeads(LeadFilterRequest filter) {
        Specification<Lead> spec = buildSpecification(filter);
        Pageable pageable = buildPageable(filter);
        Page<Lead> page = leadRepository.findAll(spec, pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public LeadResponse getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        return toResponse(lead);
    }

    @Transactional
    @CacheEvict(value = "dashboardSummary", allEntries = true)
    public LeadResponse updateStatus(Long id, LeadStatus status) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        lead.setStatus(status);
        Lead saved = leadRepository.save(lead);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "dashboardSummary", allEntries = true)
    public void deleteLead(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lead not found with id: " + id);
        }
        leadRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Lead> getAllLeadsForExport(LeadFilterRequest filter) {
        Specification<Lead> spec = buildSpecification(filter);
        Sort sort = Sort.by(Sort.Direction.DESC, "score");
        return leadRepository.findAll(spec, sort);
    }

    private Specification<Lead> buildSpecification(LeadFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Lead, Company> companyJoin = root.join("company", JoinType.INNER);
            Join<Lead, Contact> contactJoin = root.join("contact", JoinType.LEFT);

            if (filter.getSearch() != null && !filter.getSearch().trim().isBlank()) {
                String term = "%" + filter.getSearch().trim().toLowerCase() + "%";
                Predicate compName = cb.like(cb.lower(companyJoin.get("companyName")), term);
                Predicate domain = cb.like(cb.lower(companyJoin.get("domain")), term);
                Predicate contactFirst = cb.like(cb.lower(contactJoin.get("firstName")), term);
                Predicate contactLast = cb.like(cb.lower(contactJoin.get("lastName")), term);
                Predicate contactEmail = cb.like(cb.lower(contactJoin.get("email")), term);
                predicates.add(cb.or(compName, domain, contactFirst, contactLast, contactEmail));
            }

            if (filter.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), filter.getPriority()));
            }

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getIndustry() != null && !filter.getIndustry().trim().isBlank()) {
                predicates.add(cb.like(cb.lower(companyJoin.get("industry")), "%" + filter.getIndustry().trim().toLowerCase() + "%"));
            }

            if (filter.getMinScore() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("score"), filter.getMinScore()));
            }
            if (filter.getMaxScore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("score"), filter.getMaxScore()));
            }

            if (filter.getMinRevenue() != null) {
                predicates.add(cb.greaterThanOrEqualTo(companyJoin.get("estimatedRevenue"), filter.getMinRevenue()));
            }
            if (filter.getMaxRevenue() != null) {
                predicates.add(cb.lessThanOrEqualTo(companyJoin.get("estimatedRevenue"), filter.getMaxRevenue()));
            }

            if (filter.getMinEmployees() != null) {
                predicates.add(cb.greaterThanOrEqualTo(companyJoin.get("employeeCount"), filter.getMinEmployees()));
            }
            if (filter.getMaxEmployees() != null) {
                predicates.add(cb.lessThanOrEqualTo(companyJoin.get("employeeCount"), filter.getMaxEmployees()));
            }

            if (filter.getHasContact() != null) {
                if (filter.getHasContact()) {
                    predicates.add(cb.isNotNull(root.get("contact")));
                } else {
                    predicates.add(cb.isNull(root.get("contact")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable buildPageable(LeadFilterRequest filter) {
        String sortBy = filter.getSortBy();
        if (sortBy == null || sortBy.isBlank()) sortBy = "score";

        Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort;

        switch (sortBy) {
            case "estimatedRevenue" -> sort = Sort.by(direction, "company.estimatedRevenue");
            case "employeeCount" -> sort = Sort.by(direction, "company.employeeCount");
            case "companyName" -> sort = Sort.by(direction, "company.companyName");
            case "createdAt" -> sort = Sort.by(direction, "createdAt");
            default -> sort = Sort.by(direction, "score");
        }

        return PageRequest.of(Math.max(0, filter.getPage()), Math.max(1, filter.getSize()), sort);
    }

    public LeadResponse toResponse(Lead lead) {
        List<String> reasons = Collections.emptyList();
        if (lead.getScoreReasons() != null && !lead.getScoreReasons().isBlank()) {
            try {
                reasons = objectMapper.readValue(lead.getScoreReasons(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                reasons = List.of(lead.getScoreReasons());
            }
        }

        LeadResponse.CompanyDto compDto = null;
        if (lead.getCompany() != null) {
            Company c = lead.getCompany();
            compDto = LeadResponse.CompanyDto.builder()
                .id(c.getId())
                .companyName(c.getCompanyName())
                .domain(c.getDomain())
                .industry(c.getIndustry())
                .location(c.getLocation())
                .city(c.getCity())
                .state(c.getState())
                .country(c.getCountry())
                .employeeCount(c.getEmployeeCount())
                .estimatedRevenue(c.getEstimatedRevenue())
                .website(c.getWebsite())
                .description(c.getDescription())
                .build();
        }

        LeadResponse.ContactDto contDto = null;
        if (lead.getContact() != null) {
            Contact ct = lead.getContact();
            contDto = LeadResponse.ContactDto.builder()
                .id(ct.getId())
                .firstName(ct.getFirstName())
                .lastName(ct.getLastName())
                .jobTitle(ct.getJobTitle())
                .email(ct.getEmail())
                .phone(ct.getPhone())
                .linkedinUrl(ct.getLinkedinUrl())
                .emailVerified(ct.getEmailVerified())
                .phoneVerified(ct.getPhoneVerified())
                .build();
        }

        return LeadResponse.builder()
            .id(lead.getId())
            .company(compDto)
            .contact(contDto)
            .score(lead.getScore())
            .revenueScore(lead.getRevenueScore())
            .industryScore(lead.getIndustryScore())
            .locationScore(lead.getLocationScore())
            .employeeScore(lead.getEmployeeScore())
            .decisionMakerScore(lead.getDecisionMakerScore() != null ? lead.getDecisionMakerScore() : 0)
            .contactScore(lead.getContactScore() != null ? lead.getContactScore() : 0)
            .websiteScore(lead.getWebsiteScore() != null ? lead.getWebsiteScore() : 0)
            .dataQualityScore(lead.getDataQualityScore())
            .priority(lead.getPriority())
            .recommendedAction(lead.getRecommendedAction())
            .actionReason(lead.getActionReason())
            .scoreReasons(reasons)
            .aiReasoning(lead.getAiReasoning())
            .aiOutreachAngle(lead.getAiOutreachAngle())
            .status(lead.getStatus())
            .createdAt(lead.getCreatedAt())
            .updatedAt(lead.getUpdatedAt())
            .build();
    }
}
