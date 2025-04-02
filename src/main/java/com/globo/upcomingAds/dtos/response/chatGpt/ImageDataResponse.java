package com.globo.upcomingAds.dtos.response.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDataResponse {

    @JsonProperty("b64_json")
    private String b64Json;
}
