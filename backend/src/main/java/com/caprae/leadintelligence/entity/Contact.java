package com.caprae.leadintelligence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contacts", indexes = {
    @Index(name = "idx_contact_company", columnList = "company_id"),
    @Index(name = "idx_contact_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "company_id")
    private Company company;

    private String firstName;

    private String lastName;

    private String jobTitle;

    private String email;

    private String phone;

    private String linkedinUrl;

    @Builder.Default
    private Boolean emailVerified = false;

    @Builder.Default
    private Boolean phoneVerified = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
