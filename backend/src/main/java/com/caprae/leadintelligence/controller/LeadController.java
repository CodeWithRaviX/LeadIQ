package com.caprae.leadintelligence.controller;

import com.caprae.leadintelligence.dto.LeadFilterRequest;
import com.caprae.leadintelligence.dto.LeadResponse;
import com.caprae.leadintelligence.dto.LeadScoreResponse;
import com.caprae.leadintelligence.entity.LeadStatus;
import com.caprae.leadintelligence.service.ExportService;
import com.caprae.leadintelligence.service.LeadScoringService;
import com.caprae.leadintelligence.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;
    private final LeadScoringService leadScoringService;
    private final ExportService exportService;

    @GetMapping
    public ResponseEntity<Page<LeadResponse>> getLeads(LeadFilterRequest filter) {
        return ResponseEntity.ok(leadService.getLeads(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LeadResponse> updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        LeadStatus status = LeadStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(leadService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteLead(@PathVariable("id") Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Lead deleted successfully"));
    }

    @DeleteMapping("/all")
    public ResponseEntity<Map<String, Object>> clearAllLeads() {
        leadService.clearAllData();
        return ResponseEntity.ok(Map.of("success", true, "message", "All leads and database records cleared successfully"));
    }

    @PostMapping("/{id}/score")
    public ResponseEntity<LeadScoreResponse> scoreLead(@PathVariable("id") Long id) {
        return ResponseEntity.ok(leadScoringService.scoreLead(id));
    }

    @PostMapping("/score-all")
    public ResponseEntity<Map<String, Object>> scoreAllLeads() {
        int count = leadScoringService.scoreAllLeads();
        return ResponseEntity.ok(Map.of("success", true, "reScoredCount", count));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLeads(LeadFilterRequest filter) throws IOException {
        String csvData = exportService.exportLeadsToCsv(filter);
        byte[] bytes = csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lead-intelligence-export.csv\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(bytes);
    }
}
