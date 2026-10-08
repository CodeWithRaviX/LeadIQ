package com.caprae.leadintelligence.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DashboardSummaryResponse {
    private long totalLeads;
    private long qualifiedLeads;
    private long highPriority;
    private long mediumPriority;
    private long lowPriority;
    private int dataQuality; // percentage e.g. 92
    private double avgScore;
    private Map<String, Long> priorityDistribution;
    private Map<String, Long> scoreDistribution;
}
