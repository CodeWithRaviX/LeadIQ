package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;

public interface ScoringRule {
    String getRuleName();
    ScoringRuleResult evaluate(Company company, Contact contact);
}
