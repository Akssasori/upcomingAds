package com.globo.upcomingAds.services;

import com.globo.upcomingAds.client.ChatGptClient;
import com.globo.upcomingAds.dtos.Message;
import com.globo.upcomingAds.dtos.request.chatGpt.ChatGptRequestDTO;
import com.globo.upcomingAds.dtos.response.chatGpt.ChatGptResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ChatGptService {

    @Value("${chatGpt.model}")
    private String modelChatGpt;

    @Value("${chatGpt.role}")
    private String roleChatGpt;

    private final ChatGptClient chatGptClient;

    public ChatGptService(ChatGptClient chatGptClient) {
        this.chatGptClient = chatGptClient;
    }

    public String getChatResponse(String userMessage) {
        ChatGptRequestDTO build = ChatGptRequestDTO.builder()
                .model(modelChatGpt)
                .messages(Collections.singletonList(Message.builder().role(roleChatGpt).content(userMessage).build()))
                .build();
//        Message message = new Message("user", userMessage);
//        ChatGptRequestDTO request = new ChatGptRequestDTO(modelChatGpt, Collections.singletonList(message));
        ChatGptResponseDTO response = chatGptClient.getChatResponse(build);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
