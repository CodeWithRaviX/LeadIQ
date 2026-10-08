package com.caprae.leadintelligence.controller;

import com.caprae.leadintelligence.dto.AIAnalysisResponse;
import com.caprae.leadintelligence.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/leads/{id}/explanation")
    public ResponseEntity<AIAnalysisResponse> getLeadExplanation(@PathVariable("id") Long id) {
        return ResponseEntity.ok(aiService.generateLeadExplanation(id));
    }

    @PostMapping("/leads/{id}/outreach-angle")
    public ResponseEntity<AIAnalysisResponse> getOutreachAngle(@PathVariable("id") Long id) {
        return ResponseEntity.ok(aiService.generateOutreachAngle(id));
    }
}
