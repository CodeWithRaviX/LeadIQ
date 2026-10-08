package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import com.caprae.leadintelligence.entity.Lead;
import com.caprae.leadintelligence.entity.LeadPriority;
import com.caprae.leadintelligence.entity.RecommendedAction;
import com.caprae.leadintelligence.util.EmailValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringEngine {

    private final RevenueRule revenueRule;
    private final IndustryRule industryRule;
    private final LocationRule locationRule;
    private final EmployeeRule employeeRule;
    private final DecisionMakerRule decisionMakerRule;
    private final ContactRule contactRule;
    private final WebsiteRule websiteRule;
    private final DataQualityRule dataQualityRule;
    private final ObjectMapper objectMapper;

    public LeadScoreResult score(Company company, Contact contact) {
        ScoringRuleResult revRes = revenueRule.evaluate(company, contact);
        ScoringRuleResult indRes = industryRule.evaluate(company, contact);
        ScoringRuleResult locRes = locationRule.evaluate(company, contact);
        ScoringRuleResult empRes = employeeRule.evaluate(company, contact);
        ScoringRuleResult dmRes = decisionMakerRule.evaluate(company, contact);
        ScoringRuleResult contRes = contactRule.evaluate(company, contact);
        ScoringRuleResult webRes = websiteRule.evaluate(company, contact);
        ScoringRuleResult dqRes = dataQualityRule.evaluate(company, contact);

        int total = revRes.getPoints() + indRes.getPoints() + locRes.getPoints()
                  + empRes.getPoints() + dmRes.getPoints() + contRes.getPoints()
                  + webRes.getPoints() + dqRes.getPoints();

        total = Math.max(0, Math.min(100, total));

        LeadPriority priority;
        if (total >= 80) {
            priority = LeadPriority.HIGH;
        } else if (total >= 60) {
            priority = LeadPriority.MEDIUM;
        } else {
            priority = LeadPriority.LOW;
        }

        // Action Recommendation Engine
        RecommendedAction action;
        String actionReason;

        boolean hasPhone = contact != null && contact.getPhone() != null && !contact.getPhone().trim().isBlank();
        boolean hasEmail = contact != null && contact.getEmail() != null && EmailValidator.isValid(contact.getEmail());
        boolean hasContact = contact != null && (hasPhone || hasEmail);

        if (total >= 80 && hasPhone) {
            action = RecommendedAction.CALL;
            actionReason = "High acquisition fit and direct decision maker phone number available.";
        } else if (total >= 70 && hasEmail) {
            action = RecommendedAction.EMAIL;
            actionReason = "Strong acquisition profile; initiate executive introduction via email.";
        } else if (total >= 50 && !hasContact) {
            action = RecommendedAction.RESEARCH;
            actionReason = "Promising financial and industry profile; requires decision maker discovery.";
        } else if (total >= 50 && (!hasEmail || !hasPhone)) {
            action = RecommendedAction.VERIFY;
            actionReason = "Good acquisition candidate; verify direct contact channel before outreach.";
        } else {
            action = RecommendedAction.SKIP;
            actionReason = "Below target acquisition criteria or low data quality threshold.";
        }

        List<String> reasons = new ArrayList<>();
        if (revRes.getPoints() > 0) reasons.add(revRes.getReason());
        if (indRes.getPoints() > 0) reasons.add(indRes.getReason());
        if (dmRes.getPoints() > 0) reasons.add(dmRes.getReason());
        if (empRes.getPoints() > 0) reasons.add(empRes.getReason());
        if (locRes.getPoints() > 0) reasons.add(locRes.getReason());
        if (contRes.getPoints() > 0) reasons.add(contRes.getReason());
        if (webRes.getPoints() > 0) reasons.add(webRes.getReason());
        reasons.add(dqRes.getReason());

        return LeadScoreResult.builder()
            .totalScore(total)
            .revenueScore(revRes.getPoints())
            .industryScore(indRes.getPoints())
            .locationScore(locRes.getPoints())
            .employeeScore(empRes.getPoints())
            .decisionMakerScore(dmRes.getPoints())
            .contactScore(contRes.getPoints() + dmRes.getPoints()) // combined contact dimension
            .websiteScore(webRes.getPoints())
            .dataQualityScore(dqRes.getPoints())
            .priority(priority)
            .recommendedAction(action)
            .actionReason(actionReason)
            .reasons(reasons)
            .build();
    }

    public void applyScoreToLead(Lead lead, LeadScoreResult result) {
        lead.setScore(result.getTotalScore());
        lead.setRevenueScore(result.getRevenueScore());
        lead.setIndustryScore(result.getIndustryScore());
        lead.setLocationScore(result.getLocationScore());
        lead.setEmployeeScore(result.getEmployeeScore());
        lead.setDecisionMakerScore(result.getDecisionMakerScore());
        lead.setContactScore(result.getContactScore() - result.getDecisionMakerScore()); // separate contact score
        lead.setWebsiteScore(result.getWebsiteScore());
        lead.setDataQualityScore(result.getDataQualityScore());
        lead.setPriority(result.getPriority());
        lead.setRecommendedAction(result.getRecommendedAction());
        lead.setActionReason(result.getActionReason());

        try {
            lead.setScoreReasons(objectMapper.writeValueAsString(result.getReasons()));
        } catch (JsonProcessingException e) {
            lead.setScoreReasons("[]");
        }
    }
}
