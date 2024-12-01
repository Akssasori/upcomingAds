package com.globo.upcomingAds.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoiceSettingsRequestDTO {

    private double stability;

    @JsonProperty("similarity_boost")
    private double similarityBoost;
}
