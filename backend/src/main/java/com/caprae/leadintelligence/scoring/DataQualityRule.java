package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import com.caprae.leadintelligence.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataQualityRule implements ScoringRule {

    private final ValidationService validationService;

    @Override
    public String getRuleName() {
        return "Data Quality";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        int score = validationService.calculateDataQualityScore(company, contact);
        String reason;
        if (score >= 8) {
            reason = "High profile completeness and data integrity (" + score + "/10)";
        } else if (score >= 5) {
            reason = "Moderate profile completeness (" + score + "/10)";
        } else {
            reason = "Sparse firmographic data; enrichment required (" + score + "/10)";
        }
        return new ScoringRuleResult(score, 10, reason);
    }
}
