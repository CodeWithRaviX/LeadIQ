package com.caprae.leadintelligence.dto;

import com.caprae.leadintelligence.entity.LeadPriority;
import com.caprae.leadintelligence.entity.RecommendedAction;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LeadScoreResponse {
    private Long leadId;
    private Integer score;
    private LeadPriority priority;
    private RecommendedAction recommendedAction;
    private String actionReason;
    private List<String> reasons;
}
