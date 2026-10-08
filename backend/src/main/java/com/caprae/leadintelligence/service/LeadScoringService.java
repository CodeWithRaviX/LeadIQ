package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.dto.LeadScoreResponse;
import com.caprae.leadintelligence.entity.Lead;
import com.caprae.leadintelligence.exception.ResourceNotFoundException;
import com.caprae.leadintelligence.repository.LeadRepository;
import com.caprae.leadintelligence.scoring.LeadScoreResult;
import com.caprae.leadintelligence.scoring.ScoringEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadScoringService {

    private final LeadRepository leadRepository;
    private final ScoringEngine scoringEngine;

    @Transactional
    @CacheEvict(value = "dashboardSummary", allEntries = true)
    public LeadScoreResponse scoreLead(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));

        LeadScoreResult result = scoringEngine.score(lead.getCompany(), lead.getContact());
        scoringEngine.applyScoreToLead(lead, result);
        leadRepository.save(lead);

        return LeadScoreResponse.builder()
            .leadId(lead.getId())
            .score(lead.getScore())
            .priority(lead.getPriority())
            .recommendedAction(lead.getRecommendedAction())
            .actionReason(lead.getActionReason())
            .reasons(result.getReasons())
            .build();
    }

    @Transactional
    @CacheEvict(value = "dashboardSummary", allEntries = true)
    public int scoreAllLeads() {
        List<Lead> leads = leadRepository.findAll();
        for (Lead lead : leads) {
            LeadScoreResult result = scoringEngine.score(lead.getCompany(), lead.getContact());
            scoringEngine.applyScoreToLead(lead, result);
        }
        leadRepository.saveAll(leads);
        log.info("Successfully re-scored {} leads", leads.size());
        return leads.size();
    }
}
