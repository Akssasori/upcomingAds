package com.globo.upcomingAds.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Value("${api.elevenlabs.key}")
    private String API_KEY;

    @Bean
    public WebClient elevenLabsWebClient(){
        return WebClient.builder()
                .baseUrl("https://api.elevenlabs.io")
                .defaultHeader("xi-api-key", API_KEY)
                .build();
    }
}
