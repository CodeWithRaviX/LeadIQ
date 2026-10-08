package com.caprae.leadintelligence.repository;

import com.caprae.leadintelligence.entity.Lead;
import com.caprae.leadintelligence.entity.LeadPriority;
import com.caprae.leadintelligence.entity.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {

    Optional<Lead> findByCompanyId(Long companyId);

    boolean existsByCompanyId(Long companyId);

    long countByPriority(LeadPriority priority);

    long countByStatus(LeadStatus status);

    @Query("SELECT COUNT(l) FROM Lead l WHERE l.score >= 60 AND l.status != com.caprae.leadintelligence.entity.LeadStatus.DISQUALIFIED")
    long countQualifiedLeads();

    @Query("SELECT AVG(l.score) FROM Lead l")
    Double findAverageScore();

    @Query("SELECT AVG(l.dataQualityScore) FROM Lead l")
    Double findAverageDataQualityScore();

    @Query("SELECT l FROM Lead l ORDER BY l.score DESC")
    List<Lead> findAllOrderByScoreDesc();
}
