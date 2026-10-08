package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RevenueRule implements ScoringRule {

    private static final BigDecimal THREE_MILLION = BigDecimal.valueOf(3_000_000);
    private static final BigDecimal FIFTEEN_MILLION = BigDecimal.valueOf(15_000_000);
    private static final BigDecimal ONE_MILLION = BigDecimal.valueOf(1_000_000);
    private static final BigDecimal THIRTY_MILLION = BigDecimal.valueOf(30_000_000);

    @Override
    public String getRuleName() {
        return "Revenue Fit";
    }

    @Override
    public ScoringRuleResult evaluate(Company company, Contact contact) {
        if (company == null || company.getEstimatedRevenue() == null) {
            return new ScoringRuleResult(0, 20, "Estimated revenue information unavailable");
        }
        BigDecimal rev = company.getEstimatedRevenue();
        if (rev.compareTo(THREE_MILLION) >= 0 && rev.compareTo(FIFTEEN_MILLION) <= 0) {
            return new ScoringRuleResult(20, 20, "Revenue ($" + formatCurrency(rev) + ") is squarely in target acquisition sweet spot ($3M - $15M)");
        } else if ((rev.compareTo(ONE_MILLION) >= 0 && rev.compareTo(THREE_MILLION) < 0) ||
                   (rev.compareTo(FIFTEEN_MILLION) > 0 && rev.compareTo(THIRTY_MILLION) <= 0)) {
            return new ScoringRuleResult(10, 20, "Revenue ($" + formatCurrency(rev) + ") is in adjacent acceptable range ($1M - $3M or $15M - $30M)");
        } else if (rev.compareTo(THIRTY_MILLION) > 0) {
            return new ScoringRuleResult(0, 20, "Revenue exceeds lower middle market acquisition target (> $30M)");
        } else {
            return new ScoringRuleResult(0, 20, "Revenue ($" + formatCurrency(rev) + ") is below target threshold (< $1M)");
        }
    }

    private String formatCurrency(BigDecimal amount) {
        double val = amount.doubleValue();
        if (val >= 1_000_000) {
            return String.format("%.1fM", val / 1_000_000);
        } else if (val >= 1_000) {
            return String.format("%.0fK", val / 1_000);
        }
        return amount.toPlainString();
    }
}
