package com.globo.upcomingAds.controllers;

import com.globo.upcomingAds.services.ChatGptService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatGptService chatGptService;

    public ChatController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @PostMapping
    public String getResponse(@RequestBody String userMessage) {
        return chatGptService.getChatResponse(userMessage);
    }
}
