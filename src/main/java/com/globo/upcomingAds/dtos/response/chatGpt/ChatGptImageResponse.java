package com.globo.upcomingAds.dtos.response.chatGpt;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGptImageResponse {

    private long created;
    private List<ImageDataResponse> data;
}
