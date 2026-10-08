package com.caprae.leadintelligence.scoring;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoringRuleResult {
    private final int points;
    private final int maxPoints;
    private final String reason;
}
