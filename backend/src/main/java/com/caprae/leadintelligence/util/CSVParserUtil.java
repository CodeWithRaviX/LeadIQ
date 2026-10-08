package com.caprae.leadintelligence.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public final class CSVParserUtil {

    private CSVParserUtil() {}

    public static class RawLeadRecord {
        public String companyName = "";
        public String domain = "";
        public String industry = "";
        public String location = "";
        public String city = "";
        public String state = "";
        public String country = "";
        public String employeeCount = "";
        public String estimatedRevenue = "";
        public String website = "";
        public String description = "";

        public String firstName = "";
        public String lastName = "";
        public String jobTitle = "";
        public String email = "";
        public String phone = "";
        public String linkedinUrl = "";
    }

    public static List<RawLeadRecord> parseCsv(InputStream inputStream) throws IOException {
        // Strip BOM if present
        PushbackInputStream pushback = new PushbackInputStream(inputStream, 3);
        byte[] bom = new byte[3];
        int n = pushback.read(bom, 0, bom.length);
        if (n >= 3 && bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF) {
            // BOM skipped
        } else if (n > 0) {
            pushback.unread(bom, 0, n);
        }

        Reader reader = new InputStreamReader(pushback, StandardCharsets.UTF_8);
        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build();

        List<RawLeadRecord> results = new ArrayList<>();
        try (CSVParser parser = new CSVParser(reader, format)) {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            Map<String, String> aliasMapping = buildAliasMap(headerMap.keySet());

            for (CSVRecord record : parser) {
                RawLeadRecord raw = new RawLeadRecord();
                raw.companyName = getVal(record, aliasMapping, "company_name");
                raw.domain = getVal(record, aliasMapping, "domain");
                raw.industry = getVal(record, aliasMapping, "industry");
                raw.location = getVal(record, aliasMapping, "location");
                raw.city = getVal(record, aliasMapping, "city");
                raw.state = getVal(record, aliasMapping, "state");
                raw.country = getVal(record, aliasMapping, "country");
                raw.employeeCount = getVal(record, aliasMapping, "employee_count");
                raw.estimatedRevenue = getVal(record, aliasMapping, "estimated_revenue");
                raw.website = getVal(record, aliasMapping, "website");
                raw.description = getVal(record, aliasMapping, "description");

                raw.firstName = getVal(record, aliasMapping, "first_name");
                raw.lastName = getVal(record, aliasMapping, "last_name");
                raw.jobTitle = getVal(record, aliasMapping, "job_title");
                raw.email = getVal(record, aliasMapping, "email");
                raw.phone = getVal(record, aliasMapping, "phone");
                raw.linkedinUrl = getVal(record, aliasMapping, "linkedin_url");

                results.add(raw);
            }
        }
        return results;
    }

    private static String getVal(CSVRecord record, Map<String, String> aliasMap, String canonicalField) {
        String actualCol = aliasMap.get(canonicalField);
        if (actualCol != null && record.isMapped(actualCol)) {
            String val = record.get(actualCol);
            return val != null ? val.trim() : "";
        }
        return "";
    }

    private static Map<String, String> buildAliasMap(Set<String> actualHeaders) {
        Map<String, String> map = new HashMap<>();
        for (String col : actualHeaders) {
            String clean = col.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "_").replaceAll("_+", "_");
            if (clean.startsWith("_")) clean = clean.substring(1);
            if (clean.endsWith("_")) clean = clean.substring(0, clean.length() - 1);

            if (matches(clean, "company", "company_name", "companyname", "organization", "firm")) {
                map.put("company_name", col);
            } else if (matches(clean, "domain", "company_domain", "web_domain")) {
                map.put("domain", col);
            } else if (matches(clean, "website", "web", "url", "site", "company_url")) {
                map.put("website", col);
            } else if (matches(clean, "industry", "sector", "vertical", "category")) {
                map.put("industry", col);
            } else if (matches(clean, "location", "headquarters", "hq", "address")) {
                map.put("location", col);
            } else if (matches(clean, "city")) {
                map.put("city", col);
            } else if (matches(clean, "state", "province", "region")) {
                map.put("state", col);
            } else if (matches(clean, "country")) {
                map.put("country", col);
            } else if (matches(clean, "employee_count", "employees", "headcount", "staff_count", "size")) {
                map.put("employee_count", col);
            } else if (matches(clean, "estimated_revenue", "revenue", "arr", "annual_revenue", "turnover")) {
                map.put("estimated_revenue", col);
            } else if (matches(clean, "description", "about", "overview", "notes")) {
                map.put("description", col);
            } else if (matches(clean, "first_name", "firstname", "first")) {
                map.put("first_name", col);
            } else if (matches(clean, "last_name", "lastname", "last")) {
                map.put("last_name", col);
            } else if (matches(clean, "job_title", "title", "role", "position")) {
                map.put("job_title", col);
            } else if (matches(clean, "email", "work_email", "contact_email", "mail")) {
                map.put("email", col);
            } else if (matches(clean, "phone", "phone_number", "telephone", "mobile", "tel")) {
                map.put("phone", col);
            } else if (matches(clean, "linkedin_url", "linkedin", "linkedin_profile")) {
                map.put("linkedin_url", col);
            }
        }
        return map;
    }

    private static boolean matches(String clean, String... aliases) {
        for (String a : aliases) {
            if (clean.equals(a)) return true;
        }
        return false;
    }
}
