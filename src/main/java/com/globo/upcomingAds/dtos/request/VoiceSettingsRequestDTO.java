package com.globo.upcomingAds.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class VoiceSettingsRequestDTO {

    private double stability;

    @JsonProperty("similarity_boost")
    private double similarityBoost;

    @JsonProperty("style")
    private double style;

    @JsonProperty("speed")
    private double speed;

    @JsonProperty("use_speaker_boos")
    private boolean use_speaker_boos;
}
