package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import com.caprae.leadintelligence.util.NormalizationUtil;
import org.springframework.stereotype.Component;

@Component
public class WebsiteRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Website Quality";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        if (company == null) {
            return new ScoringRuleResult(0, 5, "Company web assets missing");
        }
        String dom = NormalizationUtil.normalizeDomain(company.getDomain());
        if (dom.isBlank() && company.getWebsite() != null) {
            dom = NormalizationUtil.normalizeDomain(company.getWebsite());
        }

        if (!dom.isBlank() && dom.contains(".") && dom.length() > 3) {
            return new ScoringRuleResult(5, 5, "Active web domain verified (" + dom + ")");
        } else {
            return new ScoringRuleResult(0, 5, "No active domain or website registered");
        }
    }
}
