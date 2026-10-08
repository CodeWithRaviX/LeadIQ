package com.caprae.leadintelligence.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImportResponse {
    private int totalRows;
    private int validRows;
    private int uniqueOpportunities;
    private int newOpportunities;
    private int updatedRecords;
    private int duplicatesRemoved;
    private int invalidRecords;
    private int highPriority;
    private int mediumPriority;
    private int lowPriority;
    private long processingTimeMs;
}
