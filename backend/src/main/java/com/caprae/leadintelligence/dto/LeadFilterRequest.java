package com.caprae.leadintelligence.dto;

import com.caprae.leadintelligence.entity.LeadPriority;
import com.caprae.leadintelligence.entity.LeadStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeadFilterRequest {
    private String search;
    private LeadPriority priority;
    private LeadStatus status;
    private String industry;
    private Integer minScore;
    private Integer maxScore;
    private BigDecimal minRevenue;
    private BigDecimal maxRevenue;
    private Integer minEmployees;
    private Integer maxEmployees;
    private Boolean hasContact;
    private String sortBy = "score";
    private String sortDir = "desc";
    private int page = 0;
    private int size = 15;
}
