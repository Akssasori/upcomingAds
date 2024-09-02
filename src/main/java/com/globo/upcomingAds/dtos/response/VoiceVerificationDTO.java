package com.globo.upcomingAds.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VoiceVerificationDTO {

    @JsonProperty("requires_verification")
    private boolean requires_verification;
    @JsonProperty("is_verified")
    private boolean is_verified;
    @JsonProperty("verification_failures")
    private List<Object> verification_failures;
    @JsonProperty("verification_attempts_count")
    private int verification_attempts_count;
    @JsonProperty("language")
    private String language;
    @JsonProperty("verification_attempts")
    private Object verification_attempts;

}
