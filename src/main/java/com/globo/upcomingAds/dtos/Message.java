package com.globo.upcomingAds.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Message {

    private String role; // "user", "system" ou "assistant"
    private String content;
}
