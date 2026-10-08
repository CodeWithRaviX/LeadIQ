package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import com.caprae.leadintelligence.util.EmailValidator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9.]+$");
    private static final Pattern MULTIPLIER_PATTERN = Pattern.compile("^\\$?([0-9.,]+)\\s*([kmbKMB])?.*$");
    private static final Pattern RANGE_PATTERN = Pattern.compile("^([0-9]+)\\s*-\\s*([0-9]+)$");

    public boolean isValidCompanyName(String name) {
        return name != null && !name.trim().isBlank() && name.trim().length() >= 2;
    }

    public BigDecimal parseRevenue(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String cleaned = raw.trim().replace("$", "").replace(",", "").trim();
        Matcher matcher = MULTIPLIER_PATTERN.matcher(raw.trim());
        if (matcher.matches()) {
            try {
                String numPart = matcher.group(1).replace(",", "");
                double value = Double.parseDouble(numPart);
                String suffix = matcher.group(2);
                if (suffix != null) {
                    char unit = Character.toUpperCase(suffix.charAt(0));
                    if (unit == 'K') {
                        value *= 1_000;
                    } else if (unit == 'M') {
                        value *= 1_000_000;
                    } else if (unit == 'B') {
                        value *= 1_000_000_000;
                    }
                }
                if (value >= 0) {
                    return BigDecimal.valueOf(value);
                }
            } catch (Exception ignored) {
            }
        }
        try {
            double parsed = Double.parseDouble(cleaned);
            if (parsed >= 0) {
                return BigDecimal.valueOf(parsed);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public Integer parseEmployeeCount(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String cleaned = raw.trim().replace(",", "").replace("+", "");
        Matcher rangeMatcher = RANGE_PATTERN.matcher(cleaned);
        if (rangeMatcher.matches()) {
            try {
                int low = Integer.parseInt(rangeMatcher.group(1));
                int high = Integer.parseInt(rangeMatcher.group(2));
                return (low + high) / 2;
            } catch (Exception ignored) {
            }
        }
        try {
            int val = Integer.parseInt(cleaned.split("[^0-9]")[0]);
            return val >= 0 ? val : null;
        } catch (Exception ignored) {
        }
        return null;
    }

    public boolean isValidEmail(String email) {
        return EmailValidator.isValid(email);
    }

    /**
     * Calculates data quality / completeness score from 0 to 10 points.
     */
    public int calculateDataQualityScore(Company company, Contact contact) {
        int score = 0;
        if (company != null) {
            if (isValidCompanyName(company.getCompanyName())) score += 1;
            if (company.getDomain() != null && !company.getDomain().isBlank()) score += 1;
            if (company.getIndustry() != null && !company.getIndustry().isBlank()) score += 1;
            if (company.getEstimatedRevenue() != null) score += 1;
            if (company.getEmployeeCount() != null) score += 1;
            if (company.getLocation() != null && !company.getLocation().isBlank()) score += 1;
            if (company.getWebsite() != null && !company.getWebsite().isBlank()) score += 1;
        }
        if (contact != null) {
            if (contact.getFirstName() != null && !contact.getFirstName().isBlank()) score += 1;
            if (contact.getJobTitle() != null && !contact.getJobTitle().isBlank()) score += 1;
            if (contact.getEmail() != null && isValidEmail(contact.getEmail())) score += 1;
        }
        return Math.min(10, score);
    }
}
