package com.globo.upcomingAds.configs;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientChatGptConfig {

    @Value("${api.chatGpt.key}")
    private String apiChatGptKey;

    @Bean(name = "openAiClient")
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + apiChatGptKey);
    }
}
