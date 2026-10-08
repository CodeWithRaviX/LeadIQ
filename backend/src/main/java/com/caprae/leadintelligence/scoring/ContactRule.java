package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import com.caprae.leadintelligence.util.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class ContactRule implements ScoringRule {

    @Override
    public String getRuleName() {
        return "Contact Availability";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        if (contact == null) {
            return new ScoringRuleResult(0, 10, "No contact details on file");
        }
        boolean hasValidEmail = contact.getEmail() != null && EmailValidator.isValid(contact.getEmail());
        boolean hasPhone = contact.getPhone() != null && !contact.getPhone().trim().isBlank() && contact.getPhone().trim().length() >= 7;

        if (hasValidEmail && hasPhone) {
            return new ScoringRuleResult(10, 10, "Full verified direct outreach channels available (Email + Phone)");
        } else if (hasValidEmail) {
            return new ScoringRuleResult(5, 10, "Valid executive email available (" + contact.getEmail() + ")");
        } else if (hasPhone) {
            return new ScoringRuleResult(5, 10, "Direct telephone number available (" + contact.getPhone() + ")");
        } else {
            return new ScoringRuleResult(0, 10, "Neither verified email nor phone available");
        }
    }
}
