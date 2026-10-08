package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.repository.CompanyRepository;
import com.caprae.leadintelligence.util.NormalizationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeduplicationServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    private DeduplicationService deduplicationService;

    @BeforeEach
    void setUp() {
        deduplicationService = new DeduplicationService(companyRepository);
    }

    @Test
    @DisplayName("Normalization: ACME INC, Acme Inc., and acme incorporated normalize identically")
    void testCompanyNameNormalization() {
        String n1 = NormalizationUtil.normalizeCompanyName("ACME INC");
        String n2 = NormalizationUtil.normalizeCompanyName("Acme Inc.");
        String n3 = NormalizationUtil.normalizeCompanyName("acme incorporated");
        String n4 = NormalizationUtil.normalizeCompanyName("Acme LLC");

        assertEquals("acme", n1);
        assertEquals("acme", n2);
        assertEquals("acme", n3);
        assertEquals("acme", n4);
    }

    @Test
    @DisplayName("Domain Normalization: strips https, www, path and query parameters")
    void testDomainNormalization() {
        String d1 = NormalizationUtil.normalizeDomain("https://www.acme.com/about?ref=lead");
        String d2 = NormalizationUtil.normalizeDomain("http://acme.com");
        String d3 = NormalizationUtil.normalizeDomain("WWW.ACME.COM/");

        assertEquals("acme.com", d1);
        assertEquals("acme.com", d2);
        assertEquals("acme.com", d3);
    }

    @Test
    @DisplayName("Deduplication: matches by domain in database")
    void testFindDuplicateByDomain() {
        Company existing = Company.builder()
            .id(1L)
            .companyName("Acme Corp")
            .domain("acme.com")
            .build();

        when(companyRepository.findByDomainIgnoreCase("acme.com"))
            .thenReturn(Optional.of(existing));

        Company candidate = Company.builder()
            .companyName("Acme Incorporated")
            .domain("https://acme.com")
            .build();

        Optional<Company> duplicate = deduplicationService.findDuplicate(candidate, null);

        assertTrue(duplicate.isPresent());
        assertEquals(1L, duplicate.get().getId());
    }

    @Test
    @DisplayName("Deduplication: catches duplicates in-memory within the same batch")
    void testFindDuplicateInBatch() {
        Map<String, Company> batch = new HashMap<>();
        Company first = Company.builder()
            .id(10L)
            .companyName("CloudPeak Systems")
            .domain("cloudpeak.io")
            .normalizedName("cloudpeak")
            .location("Austin, TX")
            .build();

        deduplicationService.registerInBatch(first, batch);

        Company duplicateCandidate = Company.builder()
            .companyName("CloudPeak Systems LLC")
            .domain("http://www.cloudpeak.io")
            .build();

        Optional<Company> found = deduplicationService.findDuplicate(duplicateCandidate, batch);

        assertTrue(found.isPresent());
        assertEquals(10L, found.get().getId());
    }
}
