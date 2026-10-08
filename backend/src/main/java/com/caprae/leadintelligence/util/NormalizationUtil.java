package com.caprae.leadintelligence.util;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Pattern;

public final class NormalizationUtil {

    private static final Pattern CORP_SUFFIXES = Pattern.compile(
        "\\b(incorporated|incorporation|inc\\.?|corporation|corp\\.?|limited|ltd\\.?|llc\\.?|l\\.l\\.c\\.?|llp\\.?|co\\.?|company|group|holdings|enterprises|solutions|technologies|tech|services|international|intl\\.?)\\b",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PUNCTUATION = Pattern.compile("[^a-zA-Z0-9\\s]");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    private NormalizationUtil() {}

    /**
     * Normalizes company name for deduplication comparison.
     * e.g., "ACME INC.", "Acme Inc.", "Acme Incorporated" -> "acme"
     */
    public static String normalizeCompanyName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        String cleaned = name.toLowerCase(Locale.ROOT).trim();
        // Remove corporate designators
        cleaned = CORP_SUFFIXES.matcher(cleaned).replaceAll("");
        // Remove punctuation
        cleaned = PUNCTUATION.matcher(cleaned).replaceAll(" ");
        // Collapse multiple spaces
        cleaned = MULTIPLE_SPACES.matcher(cleaned).replaceAll(" ").trim();
        return cleaned;
    }

    /**
     * Normalizes a web domain or website URL.
     * e.g. "https://www.acme.com/about?ref=1" -> "acme.com"
     */
    public static String normalizeDomain(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String cleaned = raw.trim().toLowerCase(Locale.ROOT);
        // Remove protocols
        if (cleaned.startsWith("http://")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("https://")) {
            cleaned = cleaned.substring(8);
        }
        // Remove leading www.
        if (cleaned.startsWith("www.")) {
            cleaned = cleaned.substring(4);
        }
        // Remove trailing path, query, hash
        int slashIdx = cleaned.indexOf('/');
        if (slashIdx != -1) {
            cleaned = cleaned.substring(0, slashIdx);
        }
        int queryIdx = cleaned.indexOf('?');
        if (queryIdx != -1) {
            cleaned = cleaned.substring(0, queryIdx);
        }
        int portIdx = cleaned.indexOf(':');
        if (portIdx != -1) {
            cleaned = cleaned.substring(0, portIdx);
        }
        return cleaned.trim();
    }

    /**
     * Normalizes geographic location string.
     */
    public static String normalizeLocation(String location) {
        if (location == null || location.isBlank()) {
            return "";
        }
        return location.toLowerCase(Locale.ROOT).replaceAll("[^a-zA-Z0-9\\s,]", "").replaceAll("\\s+", " ").trim();
    }
}
