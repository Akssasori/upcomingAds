package com.globo.upcomingAds.configs;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class ClientClaudeConfig {

    @Value("${api.claude.key}")
    private String apiKey;

    @Bean(name = "claudeRequestInterceptor")
    public RequestInterceptor claudeRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("x-api-key", apiKey);
            requestTemplate.header("anthropic-version", "2023-06-01");
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}
