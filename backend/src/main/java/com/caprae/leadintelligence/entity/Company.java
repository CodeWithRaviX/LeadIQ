package com.caprae.leadintelligence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_company_domain", columnList = "domain"),
    @Index(name = "idx_company_name", columnList = "companyName"),
    @Index(name = "idx_company_normalized_name", columnList = "normalizedName"),
    @Index(name = "idx_company_industry", columnList = "industry")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String domain;

    private String normalizedName;

    private String industry;

    private String location;

    private String city;

    private String state;

    private String country;

    private Integer employeeCount;

    @Column(precision = 15, scale = 2)
    private BigDecimal estimatedRevenue;

    private String website;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
