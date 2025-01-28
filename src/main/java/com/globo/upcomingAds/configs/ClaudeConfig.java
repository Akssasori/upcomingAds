package com.globo.upcomingAds.configs;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaudeConfig {

    @Value("${api.claude.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor anthropicApiKeyInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("x-api-key", apiKey);
            requestTemplate.header("anthropic-version", "2023-06-01");
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}
