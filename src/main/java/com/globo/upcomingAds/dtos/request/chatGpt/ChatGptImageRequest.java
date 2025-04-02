package com.globo.upcomingAds.dtos.request.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatGptImageRequest {

    private String prompt;
    private int n = 1;
    private String size = "1024x1024";
    @JsonProperty("response_format")
    private String responseFormat = "b64_json";
    private String model;
}
