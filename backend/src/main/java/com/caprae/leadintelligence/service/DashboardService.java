package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.dto.DashboardSummaryResponse;
import com.caprae.leadintelligence.entity.Lead;
import com.caprae.leadintelligence.entity.LeadPriority;
import com.caprae.leadintelligence.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final LeadRepository leadRepository;

    @Transactional(readOnly = true)
    @Cacheable("dashboardSummary")
    public DashboardSummaryResponse getSummary() {
        long total = leadRepository.count();
        long qualified = leadRepository.countQualifiedLeads();
        long high = leadRepository.countByPriority(LeadPriority.HIGH);
        long medium = leadRepository.countByPriority(LeadPriority.MEDIUM);
        long low = leadRepository.countByPriority(LeadPriority.LOW);

        Double avgScoreVal = leadRepository.findAverageScore();
        double avgScore = avgScoreVal != null ? Math.round(avgScoreVal * 10.0) / 10.0 : 0.0;

        Double avgDqVal = leadRepository.findAverageDataQualityScore();
        // Scale 0-10 score to percentage 0-100
        int dataQuality = avgDqVal != null ? (int) Math.round(avgDqVal * 10.0) : 0;

        Map<String, Long> priorityDist = new LinkedHashMap<>();
        priorityDist.put("HIGH", high);
        priorityDist.put("MEDIUM", medium);
        priorityDist.put("LOW", low);

        Map<String, Long> scoreDist = new LinkedHashMap<>();
        scoreDist.put("90-100", 0L);
        scoreDist.put("80-89", 0L);
        scoreDist.put("70-79", 0L);
        scoreDist.put("60-69", 0L);
        scoreDist.put("below60", 0L);

        List<Lead> allLeads = leadRepository.findAll();
        for (Lead l : allLeads) {
            int s = l.getScore();
            if (s >= 90) scoreDist.put("90-100", scoreDist.get("90-100") + 1);
            else if (s >= 80) scoreDist.put("80-89", scoreDist.get("80-89") + 1);
            else if (s >= 70) scoreDist.put("70-79", scoreDist.get("70-79") + 1);
            else if (s >= 60) scoreDist.put("60-69", scoreDist.get("60-69") + 1);
            else scoreDist.put("below60", scoreDist.get("below60") + 1);
        }

        return DashboardSummaryResponse.builder()
            .totalLeads(total)
            .qualifiedLeads(qualified)
            .highPriority(high)
            .mediumPriority(medium)
            .lowPriority(low)
            .avgScore(avgScore)
            .dataQuality(dataQuality)
            .priorityDistribution(priorityDist)
            .scoreDistribution(scoreDist)
            .build();
    }
}
