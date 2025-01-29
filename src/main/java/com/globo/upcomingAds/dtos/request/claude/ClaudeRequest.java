package com.globo.upcomingAds.dtos.request.claude;

import com.globo.upcomingAds.dtos.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ClaudeRequest {

    private String model = "claude-3-5-sonnet-20241022";
    private Integer max_tokens = 1024;
    private List<Message> messages;

    public static ClaudeRequest of(String userMessage) {
        ClaudeRequest request = new ClaudeRequest();
        request.setMessages(Collections.singletonList(
                new Message("user", userMessage)
        ));
        return request;
    }
}
