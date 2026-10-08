package com.caprae.leadintelligence.dto;

import com.caprae.leadintelligence.entity.LeadPriority;
import com.caprae.leadintelligence.entity.LeadStatus;
import com.caprae.leadintelligence.entity.RecommendedAction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LeadResponse {
    private Long id;
    private CompanyDto company;
    private ContactDto contact;
    private Integer score;
    private Integer revenueScore;
    private Integer industryScore;
    private Integer locationScore;
    private Integer employeeScore;
    private Integer decisionMakerScore;
    private Integer contactScore;
    private Integer websiteScore;
    private Integer dataQualityScore;
    private LeadPriority priority;
    private RecommendedAction recommendedAction;
    private String actionReason;
    private List<String> scoreReasons;
    private String aiReasoning;
    private String aiOutreachAngle;
    private LeadStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class CompanyDto {
        private Long id;
        private String companyName;
        private String domain;
        private String industry;
        private String location;
        private String city;
        private String state;
        private String country;
        private Integer employeeCount;
        private BigDecimal estimatedRevenue;
        private String website;
        private String description;
    }

    @Getter
    @Builder
    public static class ContactDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String jobTitle;
        private String email;
        private String phone;
        private String linkedinUrl;
        private Boolean emailVerified;
        private Boolean phoneVerified;
    }
}
