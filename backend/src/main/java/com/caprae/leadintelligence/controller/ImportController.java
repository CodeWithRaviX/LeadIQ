package com.caprae.leadintelligence.controller;

import com.caprae.leadintelligence.dto.ImportResponse;
import com.caprae.leadintelligence.service.LeadImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final LeadImportService leadImportService;

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResponse> importCsv(@RequestParam("file") MultipartFile file) {
        ImportResponse response = leadImportService.importCsv(file);
        return ResponseEntity.ok(response);
    }
}
