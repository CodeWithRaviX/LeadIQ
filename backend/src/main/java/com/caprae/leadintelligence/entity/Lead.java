package com.caprae.leadintelligence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leads", indexes = {
    @Index(name = "idx_lead_score", columnList = "score"),
    @Index(name = "idx_lead_priority", columnList = "priority"),
    @Index(name = "idx_lead_status", columnList = "status"),
    @Index(name = "idx_lead_company", columnList = "company_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Builder.Default
    @Column(nullable = false)
    private Integer score = 0;

    @Builder.Default
    private Integer revenueScore = 0;

    @Builder.Default
    private Integer industryScore = 0;

    @Builder.Default
    private Integer locationScore = 0;

    @Builder.Default
    private Integer employeeScore = 0;

    @Builder.Default
    private Integer decisionMakerScore = 0;

    @Builder.Default
    private Integer contactScore = 0;

    @Builder.Default
    private Integer websiteScore = 0;

    @Builder.Default
    private Integer dataQualityScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LeadPriority priority = LeadPriority.LOW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private RecommendedAction recommendedAction = RecommendedAction.RESEARCH;

    @Column(columnDefinition = "TEXT")
    private String actionReason;

    @Column(columnDefinition = "TEXT")
    private String scoreReasons; // JSON array of explanation strings

    @Column(columnDefinition = "TEXT")
    private String aiReasoning;

    @Column(columnDefinition = "TEXT")
    private String aiOutreachAngle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
