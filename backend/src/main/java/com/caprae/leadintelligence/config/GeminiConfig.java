package com.caprae.leadintelligence.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GeminiConfig {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isBlank() && !apiKey.contains("placeholder");
    }
}
