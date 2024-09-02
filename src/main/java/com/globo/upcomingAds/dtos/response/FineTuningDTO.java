package com.globo.upcomingAds.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FineTuningDTO {

    @JsonProperty("is_allowed_to_fine_tune")
    private boolean is_allowed_to_fine_tune;
    @JsonProperty("state")
    private Object state;
    @JsonProperty("verification_failures")
    private List<Object> verification_failures;
    @JsonProperty("verification_attempts_count")
    private int verification_attempts_count;
    @JsonProperty("manual_verification_requested")
    private boolean manual_verification_requested;
    @JsonProperty("language")
    private String language;
    @JsonProperty("progress")
    private Object progress;
    @JsonProperty("message")
    private Object message;
    @JsonProperty("dataset_duration_seconds")
    private Object dataset_duration_seconds;
    @JsonProperty("verification_attempts")
    private Object verification_attempts;
    @JsonProperty("slice_ids")
    private Object slice_ids;
    @JsonProperty("manual_verification")
    private Object manual_verification;

}
