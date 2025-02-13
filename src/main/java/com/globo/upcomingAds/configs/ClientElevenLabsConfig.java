package com.globo.upcomingAds.configs;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration
public class ClientElevenLabsConfig {

    @Value("${api.elevenlabs.key}")
    private String apiKey;

    @Bean(name = "elevenLabsRequestInterceptor")
    public RequestInterceptor elevenLabsRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("xi-api-key", apiKey);
            requestTemplate.header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        };
    }
}
