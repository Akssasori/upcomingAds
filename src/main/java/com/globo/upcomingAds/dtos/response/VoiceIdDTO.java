package com.globo.upcomingAds.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class VoiceIdDTO {

    @JsonProperty("voice_id")
    private String voiceId;

    @JsonProperty("requires_verification")
    private boolean requiresVerification;

}
