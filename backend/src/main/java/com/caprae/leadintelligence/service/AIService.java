package com.caprae.leadintelligence.service;

import com.caprae.leadintelligence.config.GeminiConfig;
import com.caprae.leadintelligence.dto.AIAnalysisResponse;
import com.caprae.leadintelligence.entity.Company;
import com.caprae.leadintelligence.entity.Contact;
import com.caprae.leadintelligence.entity.Lead;
import com.caprae.leadintelligence.exception.ResourceNotFoundException;
import com.caprae.leadintelligence.repository.LeadRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final GeminiConfig geminiConfig;
    private final LeadRepository leadRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Transactional
    public AIAnalysisResponse generateLeadExplanation(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));

        // Return cached insight if already generated
        if (lead.getAiReasoning() != null && !lead.getAiReasoning().isBlank()) {
            return AIAnalysisResponse.builder()
                .leadId(lead.getId())
                .type("EXPLANATION")
                .content(lead.getAiReasoning())
                .generatedAt(LocalDateTime.now())
                .build();
        }

        String explanation = null;
        if (geminiConfig.isConfigured()) {
            explanation = callGeminiForExplanation(lead);
        }

        if (explanation == null || explanation.isBlank()) {
            explanation = generateFallbackExplanation(lead);
        }

        lead.setAiReasoning(explanation);
        leadRepository.save(lead);

        return AIAnalysisResponse.builder()
            .leadId(lead.getId())
            .type("EXPLANATION")
            .content(explanation)
            .generatedAt(LocalDateTime.now())
            .build();
    }

    @Transactional
    public AIAnalysisResponse generateOutreachAngle(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + leadId));

        if (lead.getAiOutreachAngle() != null && !lead.getAiOutreachAngle().isBlank()) {
            return AIAnalysisResponse.builder()
                .leadId(lead.getId())
                .type("OUTREACH_ANGLE")
                .content(lead.getAiOutreachAngle())
                .generatedAt(LocalDateTime.now())
                .build();
        }

        String angle = null;
        if (geminiConfig.isConfigured()) {
            angle = callGeminiForOutreachAngle(lead);
        }

        if (angle == null || angle.isBlank()) {
            angle = generateFallbackOutreachAngle(lead);
        }

        lead.setAiOutreachAngle(angle);
        leadRepository.save(lead);

        return AIAnalysisResponse.builder()
            .leadId(lead.getId())
            .type("OUTREACH_ANGLE")
            .content(angle)
            .generatedAt(LocalDateTime.now())
            .build();
    }

    private String callGeminiForExplanation(Lead lead) {
        Company comp = lead.getCompany();
        Contact cont = lead.getContact();

        String prompt = String.format(
            "Analyze this target acquisition lead for a private equity / search fund:\n" +
            "Company: %s\nIndustry: %s\nEstimated Revenue: %s\nEmployees: %s\nLocation: %s\nContact: %s (%s)\nScore: %d/100 (%s priority)\n" +
            "Score Reasons: %s\n\n" +
            "In 2-3 concise, professional sentences, explain why this company is attractive for acquisition or what risk profile it represents. Focus on business fundamentals and financial scale.",
            comp != null ? comp.getCompanyName() : "Unknown",
            comp != null ? comp.getIndustry() : "Unknown",
            comp != null && comp.getEstimatedRevenue() != null ? "$" + comp.getEstimatedRevenue() : "N/A",
            comp != null && comp.getEmployeeCount() != null ? comp.getEmployeeCount() : "N/A",
            comp != null ? comp.getLocation() : "Unknown",
            cont != null ? cont.getFirstName() + " " + cont.getLastName() : "Unassigned",
            cont != null ? cont.getJobTitle() : "N/A",
            lead.getScore(),
            lead.getPriority(),
            lead.getScoreReasons() != null ? lead.getScoreReasons() : "N/A"
        );

        return requestGemini(prompt, "You are an experienced M&A private equity analyst. Provide direct, objective investment thesis summaries.");
    }

    private String callGeminiForOutreachAngle(Lead lead) {
        Company comp = lead.getCompany();
        Contact cont = lead.getContact();

        String prompt = String.format(
            "Company: %s (%s)\nRevenue: %s\nDecision Maker: %s (%s)\nAcquisition Score: %d/100\n\n" +
            "Write a 2-4 sentence tailored outreach angle for an acquisition professional speaking with this business owner. " +
            "Do NOT write an email template. Write a strategic conversation angle (e.g., highlighting growth opportunities, owner transition objectives, or operational scale).",
            comp != null ? comp.getCompanyName() : "The target company",
            comp != null ? comp.getIndustry() : "Technology",
            comp != null && comp.getEstimatedRevenue() != null ? "$" + comp.getEstimatedRevenue() : "confidential revenue scale",
            cont != null ? cont.getFirstName() + " " + cont.getLastName() : "the principal",
            cont != null ? cont.getJobTitle() : "executive",
            lead.getScore()
        );

        return requestGemini(prompt, "You are a senior M&A origination advisor crafting discrete, high-credibility outreach angles for business owners.");
    }

    private String requestGemini(String userPrompt, String systemPrompt) {
        try {
            WebClient client = webClientBuilder.build();
            String fullUrl = String.format("%s/%s:generateContent", geminiConfig.getApiUrl(), geminiConfig.getModel());

            Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", systemPrompt))
            );

            Map<String, Object> contents = Map.of(
                "parts", List.of(Map.of("text", userPrompt))
            );

            Map<String, Object> generationConfig = Map.of(
                "temperature", 0.4,
                "maxOutputTokens", 300
            );

            Map<String, Object> body = Map.of(
                "system_instruction", systemInstruction,
                "contents", List.of(contents),
                "generationConfig", generationConfig
            );

            String responseJson = client.post()
                .uri(fullUrl)
                .header("x-goog-api-key", geminiConfig.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(12))
                .block();

            if (responseJson != null) {
                JsonNode root = objectMapper.readTree(responseJson);
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && !candidates.isEmpty()) {
                    JsonNode parts = candidates.get(0).path("content").path("parts");
                    if (parts.isArray() && !parts.isEmpty()) {
                        return parts.get(0).path("text").asText().trim();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Gemini API call failed or timed out. Falling back to analytical engine: {}", e.getMessage());
        }
        return null;
    }

    private String generateFallbackExplanation(Lead lead) {
        Company c = lead.getCompany();
        String name = c != null ? c.getCompanyName() : "This target company";
        String ind = c != null && c.getIndustry() != null && !c.getIndustry().isBlank() ? c.getIndustry() : "its operational sector";
        String rev = c != null && c.getEstimatedRevenue() != null ? "$" + formatRev(c.getEstimatedRevenue().doubleValue()) : null;

        StringBuilder sb = new StringBuilder();
        if (lead.getScore() >= 80) {
            sb.append(name).append(" is a high-conviction acquisition prospect within ").append(ind);
            if (rev != null) {
                sb.append(" generating an estimated ").append(rev).append(" in annual revenue, aligning cleanly with core buyout criteria.");
            } else {
                sb.append(", demonstrating strong operational positioning.");
            }
            sb.append(" Its combination of verified executive leadership and strategic vertical fit makes it a prime candidate for immediate proprietary origination.");
        } else if (lead.getScore() >= 60) {
            sb.append(name).append(" presents an attractive secondary opportunity within ").append(ind).append(". ");
            sb.append("While fundamental financial scale and market presence are promising, further enrichment of the decision-maker contact tree is recommended prior to formal deployment.");
        } else {
            sb.append(name).append(" currently demonstrates lower alignment with our target acquisition buy box due to sub-scale financial metrics or misaligned vertical focus. ");
            sb.append("Recommended for ongoing monitoring rather than active capital allocation.");
        }
        return sb.toString();
    }

    private String generateFallbackOutreachAngle(Lead lead) {
        Company c = lead.getCompany();
        Contact cont = lead.getContact();
        String name = c != null ? c.getCompanyName() : "the business";
        String contactName = cont != null && cont.getFirstName() != null && !cont.getFirstName().isBlank() ? cont.getFirstName() : "the business owner";

        if (lead.getScore() >= 80) {
            return String.format(
                "%s appears to fit the target acquisition profile because of its stable operational scale and strong industry tailwinds. " +
                "Lead with a confidential conversation with %s regarding long-term ownership transition and growth capital objectives rather than immediately presenting a rigid buyout proposal.",
                name, contactName
            );
        } else if (lead.getScore() >= 60) {
            return String.format(
                "Focus the initial inquiry on industry market dynamics and operational scaling milestones achieved at %s. " +
                "Inquire about %s's strategic roadmap for the coming 24 months to assess whether an equity partnership or recapitalization aligns with their objectives.",
                name, contactName
            );
        } else {
            return String.format(
                "Position this contact as an industry research benchmark regarding trends in %s. " +
                "Establish a baseline relationship with %s without committing acquisition resources until scale metrics improve.",
                c != null && c.getIndustry() != null ? c.getIndustry() : "the market", contactName
            );
        }
    }

    private String formatRev(double val) {
        if (val >= 1_000_000) return String.format("%.1fM", val / 1_000_000);
        if (val >= 1_000) return String.format("%.0fK", val / 1_000);
        return String.valueOf((long) val);
    }
}
