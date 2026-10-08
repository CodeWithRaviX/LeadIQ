package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class DecisionMakerRule implements ScoringRule {

    private static final Set<String> OWNER_TITLES = Set.of(
        "owner", "founder", "co-founder", "ceo", "chief executive officer",
        "president", "managing partner", "principal"
    );

    private static final Set<String> CSUITE_TITLES = Set.of(
        "coo", "cfo", "chief operating officer", "chief financial officer",
        "managing director", "partner"
    );

    private static final Set<String> OPERATIONAL_TITLES = Set.of(
        "cto", "chief technology officer", "vice president", "vp",
        "director", "general manager", "head of"
    );

    @Override
    public String getRuleName() {
        return "Owner / Decision Maker";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        if (contact == null || contact.getJobTitle() == null || contact.getJobTitle().isBlank()) {
            return new ScoringRuleResult(0, 15, "No executive contact or job title identified");
        }
        String title = contact.getJobTitle().toLowerCase(Locale.ROOT);
        for (String owner : OWNER_TITLES) {
            if (title.contains(owner)) {
                return new ScoringRuleResult(15, 15, "Direct business owner/CEO identified (" + contact.getJobTitle() + ")");
            }
        }
        for (String csuite : CSUITE_TITLES) {
            if (title.contains(csuite)) {
                return new ScoringRuleResult(10, 15, "C-Suite executive decision maker identified (" + contact.getJobTitle() + ")");
            }
        }
        for (String op : OPERATIONAL_TITLES) {
            if (title.contains(op)) {
                return new ScoringRuleResult(3, 15, "Operational/department manager identified (" + contact.getJobTitle() + ")");
            }
        }
        return new ScoringRuleResult(0, 15, "Contact identified (" + contact.getJobTitle() + ") is non-executive");
    }
}
