package com.globo.upcomingAds.configs;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientChatGptConfig {

    @Value("${api.chatGpt.key}")
    private String apiChatGptKey;

    @Bean(name = "chatGptRequestInterceptor")
    public RequestInterceptor chatGptRequestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + apiChatGptKey);
    }
}
