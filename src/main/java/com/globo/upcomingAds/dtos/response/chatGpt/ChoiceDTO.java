package com.globo.upcomingAds.dtos.response.chatGpt;

import com.globo.upcomingAds.dtos.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceDTO {

    private Message message;
}
