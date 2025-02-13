package com.globo.upcomingAds.client;

import com.globo.upcomingAds.configs.ClientClaudeConfig;
import com.globo.upcomingAds.configs.ClientElevenLabsConfig;
import com.globo.upcomingAds.dtos.response.claude.ClaudeResponse;
import com.globo.upcomingAds.dtos.request.claude.ClaudeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${ai.claude.name}",
        url = "${ai.claude.url}",configuration = ClientClaudeConfig.class)
public interface ClaudeClient {

    @PostMapping("/messages")
    ClaudeResponse sendMessage(@RequestBody ClaudeRequest request);

}
