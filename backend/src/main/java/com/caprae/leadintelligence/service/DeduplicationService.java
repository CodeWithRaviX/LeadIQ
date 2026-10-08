package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.repository.CompanyRepository;
import com.caprae.leadintelligence.util.NormalizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeduplicationService {

    private final CompanyRepository companyRepository;

    /**
     * Looks up an existing company using primary (domain) or secondary (normalized name + location) identifiers.
     * Also checks an optional in-memory batch map for deduplicating within the same import run.
     */
    public Optional<Company> findDuplicate(Company candidate, Map<String, Company> batchIndex) {
        String domain = NormalizationUtil.normalizeDomain(candidate.getDomain());
        if (domain.isBlank() && candidate.getWebsite() != null) {
            domain = NormalizationUtil.normalizeDomain(candidate.getWebsite());
        }

        String normName = candidate.getNormalizedName();
        if (normName == null || normName.isBlank()) {
            normName = NormalizationUtil.normalizeCompanyName(candidate.getCompanyName());
        }

        String normLoc = NormalizationUtil.normalizeLocation(candidate.getLocation());

        // 1. Check in-memory batch index first (for intra-CSV duplicates)
        if (batchIndex != null) {
            if (!domain.isBlank() && batchIndex.containsKey("dom:" + domain)) {
                return Optional.of(batchIndex.get("dom:" + domain));
            }
            if (domain.isBlank()) {
                if (!normName.isBlank() && !normLoc.isBlank() && batchIndex.containsKey("nameloc:" + normName + "|" + normLoc)) {
                    return Optional.of(batchIndex.get("nameloc:" + normName + "|" + normLoc));
                }
                if (!normName.isBlank() && batchIndex.containsKey("name:" + normName)) {
                    return Optional.of(batchIndex.get("name:" + normName));
                }
            }
        }

        // 2. Check Database by domain (Primary unique identifier)
        if (!domain.isBlank()) {
            Optional<Company> byDomain = companyRepository.findByDomainIgnoreCase(domain);
            if (byDomain.isPresent()) {
                log.debug("Duplicate detected by domain: {}", domain);
                return byDomain;
            }
            // If domain is provided but not found in DB, it is a unique company (do not match to different domains)
            return Optional.empty();
        }

        // 3. Fallback for domain-less records: Check Database by normalized name + location
        if (!normName.isBlank() && !normLoc.isBlank()) {
            Optional<Company> byNameLoc = companyRepository.findByNormalizedNameIgnoreCaseAndLocationIgnoreCase(normName, normLoc);
            if (byNameLoc.isPresent()) {
                log.debug("Duplicate detected by normalized name + location: {} in {}", normName, normLoc);
                return byNameLoc;
            }
        }

        // 4. Check Database by normalized name alone if location is absent or identical
        if (!normName.isBlank()) {
            Optional<Company> byName = companyRepository.findByNormalizedNameIgnoreCase(normName);
            if (byName.isPresent()) {
                Company existing = byName.get();
                if (existing.getLocation() == null || candidate.getLocation() == null ||
                    existing.getLocation().equalsIgnoreCase(candidate.getLocation())) {
                    log.debug("Duplicate detected by normalized name: {}", normName);
                    return byName;
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Registers a company into the batch index to catch intra-file duplicates.
     */
    public void registerInBatch(Company company, Map<String, Company> batchIndex) {
        if (batchIndex == null || company == null) return;
        String domain = NormalizationUtil.normalizeDomain(company.getDomain());
        if (!domain.isBlank()) {
            batchIndex.put("dom:" + domain, company);
        }
        String normName = company.getNormalizedName();
        String normLoc = NormalizationUtil.normalizeLocation(company.getLocation());
        if (!normName.isBlank() && !normLoc.isBlank()) {
            batchIndex.put("nameloc:" + normName + "|" + normLoc, company);
        }
        if (!normName.isBlank()) {
            batchIndex.put("name:" + normName, company);
        }
    }
}
