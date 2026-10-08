package com.caprae.leadintelligence.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AIAnalysisResponse {
    private Long leadId;
    private String type; // EXPLANATION or OUTREACH_ANGLE
    private String content;
    private LocalDateTime generatedAt;
}
