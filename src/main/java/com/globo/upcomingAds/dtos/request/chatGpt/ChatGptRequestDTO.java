package com.globo.upcomingAds.dtos.request.chatGpt;

import com.globo.upcomingAds.dtos.Message;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatGptRequestDTO {

    private String model; // Modelo, como "gpt-3.5-turbo"
    private List<Message> messages;
}
