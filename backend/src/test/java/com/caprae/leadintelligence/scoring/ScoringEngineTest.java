package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.*;
import com.caprae.leadintelligence.service.ValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ScoringEngineTest {

    private ScoringEngine scoringEngine;
    private RevenueRule revenueRule;
    private IndustryRule industryRule;
    private LocationRule locationRule;
    private EmployeeRule employeeRule;
    private DecisionMakerRule decisionMakerRule;
    private ContactRule contactRule;
    private WebsiteRule websiteRule;
    private DataQualityRule dataQualityRule;

    @BeforeEach
    void setUp() {
        ValidationService validationService = new ValidationService();
        revenueRule = new RevenueRule();
        industryRule = new IndustryRule();
        locationRule = new LocationRule();
        employeeRule = new EmployeeRule();
        decisionMakerRule = new DecisionMakerRule();
        contactRule = new ContactRule();
        websiteRule = new WebsiteRule();
        dataQualityRule = new DataQualityRule(validationService);

        scoringEngine = new ScoringEngine(
            revenueRule,
            industryRule,
            locationRule,
            employeeRule,
            decisionMakerRule,
            contactRule,
            websiteRule,
            dataQualityRule,
            new ObjectMapper()
        );
    }

    @Test
    @DisplayName("Revenue Rule: Target range ($8.2M) should receive full 20 points")
    void testRevenueRuleSweetSpot() {
        Company company = Company.builder()
            .companyName("Acme Corp")
            .estimatedRevenue(BigDecimal.valueOf(8_200_000))
            .build();
        ScoringRuleResult result = revenueRule.evaluate(company, null);
        assertEquals(20, result.getPoints());
    }

    @Test
    @DisplayName("Revenue Rule: Under threshold ($500K) should receive 0 points")
    void testRevenueRuleLow() {
        Company company = Company.builder()
            .companyName("Tiny LLC")
            .estimatedRevenue(BigDecimal.valueOf(500_000))
            .build();
        ScoringRuleResult result = revenueRule.evaluate(company, null);
        assertEquals(0, result.getPoints());
    }

    @Test
    @DisplayName("Industry Rule: Target vertical (B2B SaaS) should receive 20 points")
    void testIndustryRuleTarget() {
        Company company = Company.builder()
            .industry("B2B SaaS & Cloud Software")
            .build();
        ScoringRuleResult result = industryRule.evaluate(company, null);
        assertEquals(20, result.getPoints());
    }

    @Test
    @DisplayName("Decision Maker Rule: Founder & CEO should receive 15 points")
    void testDecisionMakerRule() {
        Contact contact = Contact.builder()
            .jobTitle("Founder & CEO")
            .build();
        ScoringRuleResult result = decisionMakerRule.evaluate(null, contact);
        assertEquals(15, result.getPoints());
    }

    @Test
    @DisplayName("High Quality Lead: Total score >= 80, Priority HIGH, Action CALL")
    void testHighQualityLeadScoring() {
        Company company = Company.builder()
            .companyName("AlphaScale Software")
            .domain("alphascale.com")
            .website("https://alphascale.com")
            .industry("B2B SaaS")
            .location("Austin, Texas")
            .country("USA")
            .employeeCount(45)
            .estimatedRevenue(BigDecimal.valueOf(8_500_000))
            .build();

        Contact contact = Contact.builder()
            .firstName("Sarah")
            .lastName("Jenkins")
            .jobTitle("Chief Executive Officer & Founder")
            .email("s.jenkins@alphascale.com")
            .phone("+1-512-555-0199")
            .build();

        LeadScoreResult result = scoringEngine.score(company, contact);

        assertTrue(result.getTotalScore() >= 80, "Expected score >= 80, got " + result.getTotalScore());
        assertEquals(LeadPriority.HIGH, result.getPriority());
        assertEquals(RecommendedAction.CALL, result.getRecommendedAction());
        assertFalse(result.getReasons().isEmpty());
    }

    @Test
    @DisplayName("Poor Quality Lead: Total score < 60, Priority LOW, Action SKIP")
    void testPoorQualityLeadScoring() {
        Company company = Company.builder()
            .companyName("Bob's Local Carpentry")
            .industry("Woodworking")
            .estimatedRevenue(BigDecimal.valueOf(250_000))
            .employeeCount(3)
            .build();

        LeadScoreResult result = scoringEngine.score(company, null);

        assertTrue(result.getTotalScore() < 60, "Expected score < 60, got " + result.getTotalScore());
        assertEquals(LeadPriority.LOW, result.getPriority());
        assertEquals(RecommendedAction.SKIP, result.getRecommendedAction());
    }
}
