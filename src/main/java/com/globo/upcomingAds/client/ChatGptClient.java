package com.globo.upcomingAds.client;

import com.globo.upcomingAds.configs.ClientChatGptConfig;
import com.globo.upcomingAds.dtos.request.chatGpt.ChatGptImageRequest;
import com.globo.upcomingAds.dtos.request.chatGpt.ChatGptRequestDTO;
import com.globo.upcomingAds.dtos.response.chatGpt.ChatGptImageResponse;
import com.globo.upcomingAds.dtos.response.chatGpt.ChatGptResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${ai.chatGpt.name}", url = "${ai.chatGpt.url}", configuration = ClientChatGptConfig.class)
public interface ChatGptClient {

    @PostMapping("/chat/completions")
    ChatGptResponseDTO getChatResponse(@RequestBody ChatGptRequestDTO request);

    @PostMapping("images/generations")
    ChatGptImageResponse generateImage(@RequestBody ChatGptImageRequest request);

}
