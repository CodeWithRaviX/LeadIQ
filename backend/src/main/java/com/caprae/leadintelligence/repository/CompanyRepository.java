package com.caprae.leadintelligence.repository;

import com.caprae.leadintelligence.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByDomainIgnoreCase(String domain);
    Optional<Company> findByNormalizedNameIgnoreCaseAndLocationIgnoreCase(String normalizedName, String location);
    Optional<Company> findByNormalizedNameIgnoreCase(String normalizedName);
}
