package com.globo.upcomingAds.dtos.response.claude;

import com.globo.upcomingAds.dtos.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClaudeResponse {

    private String id;
    private List<Message> content;
    private String role;
    private String model;
}
