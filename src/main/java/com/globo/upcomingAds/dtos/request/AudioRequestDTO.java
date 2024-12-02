package com.globo.upcomingAds.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AudioRequestDTO {

    private String text;

    @JsonProperty("voice_settings")
    private VoiceSettingsRequestDTO voiceSettings;

    @JsonProperty("model_id")
    private String modelId;

    @JsonProperty("language_code")
    private String languageCode;
}
