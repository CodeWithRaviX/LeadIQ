package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.dto.LeadFilterRequest;
import com.caprae.leadintelligence.entity.Lead;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final LeadService leadService;

    public String exportLeadsToCsv(LeadFilterRequest filter) throws IOException {
        List<Lead> leads = leadService.getAllLeadsForExport(filter);

        StringWriter writer = new StringWriter();
        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader("Company", "Domain", "Industry", "Revenue", "Employees", "Location",
                       "Contact Name", "Job Title", "Email", "Phone",
                       "Score", "Priority", "Recommended Action", "Action Reason", "Status")
            .build();

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (Lead l : leads) {
                String compName = l.getCompany() != null ? l.getCompany().getCompanyName() : "";
                String domain = l.getCompany() != null ? l.getCompany().getDomain() : "";
                String industry = l.getCompany() != null ? l.getCompany().getIndustry() : "";
                String rev = (l.getCompany() != null && l.getCompany().getEstimatedRevenue() != null)
                    ? "$" + l.getCompany().getEstimatedRevenue().toPlainString() : "";
                String emp = (l.getCompany() != null && l.getCompany().getEmployeeCount() != null)
                    ? String.valueOf(l.getCompany().getEmployeeCount()) : "";
                String loc = l.getCompany() != null ? l.getCompany().getLocation() : "";

                String contactName = "";
                String title = "";
                String email = "";
                String phone = "";
                if (l.getContact() != null) {
                    contactName = (l.getContact().getFirstName() + " " + l.getContact().getLastName()).trim();
                    title = l.getContact().getJobTitle() != null ? l.getContact().getJobTitle() : "";
                    email = l.getContact().getEmail() != null ? l.getContact().getEmail() : "";
                    phone = l.getContact().getPhone() != null ? l.getContact().getPhone() : "";
                }

                printer.printRecord(
                    compName,
                    domain,
                    industry,
                    rev,
                    emp,
                    loc,
                    contactName,
                    title,
                    email,
                    phone,
                    l.getScore(),
                    l.getPriority(),
                    l.getRecommendedAction(),
                    l.getActionReason(),
                    l.getStatus()
                );
            }
        }

        return writer.toString();
    }
}
