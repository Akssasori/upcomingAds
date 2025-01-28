package com.globo.upcomingAds.controllers;

import com.globo.upcomingAds.services.ClaudeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("claude")
public class ClaudeController {

    private final ClaudeService claudeService;

    public ClaudeController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String message) {
        String response = claudeService.sendMessage(message);
        return ResponseEntity.ok(response);
    }

}
