package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class IndustryRule implements ScoringRule {

    private static final Set<String> TARGET_KEYWORDS = Set.of(
        "saas", "software", "healthtech", "fintech", "govtech",
        "cybersecurity", "compliance", "cloud", "logistics tech", "proptech",
        "b2b saas", "enterprise software", "ai/ml", "supply chain tech"
    );

    private static final Set<String> ADJACENT_KEYWORDS = Set.of(
        "marketing tech", "martech", "edtech", "ecommerce", "e-commerce",
        "data services", "telecom"
    );

    private static final Set<String> SERVICE_AGENCY_KEYWORDS = Set.of(
        "it services", "managed services", "digital agency", "consulting",
        "technology services"
    );

    @Override
    public String getRuleName() {
        return "Industry Fit";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        if (company == null || company.getIndustry() == null || company.getIndustry().isBlank()) {
            return new ScoringRuleResult(0, 20, "Industry vertical unspecified");
        }
        String ind = company.getIndustry().toLowerCase(Locale.ROOT);
        for (String target : TARGET_KEYWORDS) {
            if (ind.contains(target)) {
                return new ScoringRuleResult(20, 20, "Industry vertical '" + company.getIndustry() + "' matches primary acquisition thesis");
            }
        }
        for (String adj : ADJACENT_KEYWORDS) {
            if (ind.contains(adj)) {
                return new ScoringRuleResult(10, 20, "Industry vertical '" + company.getIndustry() + "' is in adjacent target vertical");
            }
        }
        for (String service : SERVICE_AGENCY_KEYWORDS) {
            if (ind.contains(service)) {
                return new ScoringRuleResult(5, 20, "Services/Agency model '" + company.getIndustry() + "' has lower recurring revenue multiple");
            }
        }
        return new ScoringRuleResult(0, 20, "Industry '" + company.getIndustry() + "' is outside strategic focus areas");
    }
}
