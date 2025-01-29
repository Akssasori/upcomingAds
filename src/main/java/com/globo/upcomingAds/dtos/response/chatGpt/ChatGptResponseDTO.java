package com.globo.upcomingAds.dtos.response.chatGpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptResponseDTO {

    private List<ChoiceDTO> choices;
}
