package com.globo.upcomingAds.client;

import com.globo.upcomingAds.configs.FeignClientChatGptConfig;
import com.globo.upcomingAds.dtos.request.chatGpt.ChatGptRequestDTO;
import com.globo.upcomingAds.dtos.response.chatGpt.ChatGptResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "${ai.chatGpt.name}", url = "${ai.chatGpt.url}", configuration = FeignClientChatGptConfig.class)
public interface ChatGptClient {

    @PostMapping("/chat/completions")
    ChatGptResponseDTO getChatResponse(@RequestBody ChatGptRequestDTO request);
}
