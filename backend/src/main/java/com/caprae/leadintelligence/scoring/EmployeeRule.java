package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Employee Count";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        if (company == null || company.getEmployeeCount() == null) {
            return new ScoringRuleResult(0, 10, "Headcount data unavailable");
        }
        int count = company.getEmployeeCount();
        if (count >= 20 && count <= 100) {
            return new ScoringRuleResult(10, 10, "Team size (" + count + " employees) is in the sweet spot for scalable acquisition (20-100)");
        } else if ((count >= 10 && count < 20) || (count > 100 && count <= 250)) {
            return new ScoringRuleResult(5, 10, "Team size (" + count + " employees) is in acceptable operational range");
        } else {
            return new ScoringRuleResult(0, 10, "Headcount (" + count + ") falls outside target lower-middle market parameters");
        }
    }
}
