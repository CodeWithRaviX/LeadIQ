package com.caprae.leadintelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LeadIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeadIntelligenceApplication.class, args);
    }
}
