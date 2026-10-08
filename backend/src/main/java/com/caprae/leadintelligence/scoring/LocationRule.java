package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class LocationRule implements ScoringRule {

    private static final Set<String> PRIMARY_GEO = Set.of(
        "san francisco", "sf", "new york", "ny", "austin", "boston",
        "seattle", "chicago", "silicon valley", "massachusetts"
    );

    private static final Set<String> SECONDARY_GEO = Set.of(
        "atlanta", "dallas", "denver", "toronto", "london", "florida",
        "georgia", "texas", "colorado", "canada", "united states", "usa", "uk"
    );

    @Override
    public String getRuleName() {
        return "Geography Fit";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        if (company == null) {
            return new ScoringRuleResult(0, 10, "Location information missing");
        }
        String loc = "";
        if (company.getLocation() != null) loc += " " + company.getLocation();
        if (company.getCity() != null) loc += " " + company.getCity();
        if (company.getState() != null) loc += " " + company.getState();
        if (company.getCountry() != null) loc += " " + company.getCountry();

        String lower = loc.toLowerCase(Locale.ROOT).trim();
        if (lower.isBlank()) {
            return new ScoringRuleResult(0, 10, "Location unverified");
        }
        for (String target : PRIMARY_GEO) {
            if (lower.contains(target)) {
                return new ScoringRuleResult(10, 10, "Location matches primary tier-1 tech market (" + loc.trim() + ")");
            }
        }
        for (String sec : SECONDARY_GEO) {
            if (lower.contains(sec)) {
                return new ScoringRuleResult(5, 10, "Location matches secondary target market (" + loc.trim() + ")");
            }
        }
        return new ScoringRuleResult(2, 10, "Operating in tertiary/international geography");
    }
}
