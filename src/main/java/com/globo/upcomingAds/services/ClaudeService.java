package com.globo.upcomingAds.services;

import com.globo.upcomingAds.client.ClaudeClient;
import com.globo.upcomingAds.dtos.response.claude.ClaudeResponse;
import com.globo.upcomingAds.dtos.request.claude.ClaudeRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClaudeService {

    private final ClaudeClient claudeClient;

    public ClaudeService(ClaudeClient claudeClient) {
        this.claudeClient = claudeClient;
    }

    public String sendMessage(String message) {
        try {
            ClaudeResponse response = claudeClient.sendMessage(ClaudeRequest.of(message));
            return response.getContent().getFirst().getContent();
        } catch (FeignException e) {
            log.error("Erro ao chamar API do Claude: {}", e.getMessage());
            throw new RuntimeException("Erro na comunicação com Claude: " + e.getMessage());
        }
    }
}
