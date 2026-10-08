package com.caprae.leadintelligence.scoring;

import com.caprae.leadintelligence.entity.LeadPriority;
import com.caprae.leadintelligence.entity.RecommendedAction;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LeadScoreResult {
    private final int totalScore;
    private final int revenueScore;
    private final int industryScore;
    private final int locationScore;
    private final int employeeScore;
    private final int contactScore;
    private final int decisionMakerScore;
    private final int websiteScore;
    private final int dataQualityScore;
    private final LeadPriority priority;
    private final RecommendedAction recommendedAction;
    private final String actionReason;
    private final List<String> reasons;
}
