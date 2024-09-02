package com.globo.upcomingAds.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VoiceDTO {

    @JsonProperty("voice_id")
    private String voice_id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("samples")
    private Object samples;
    @JsonProperty("category")
    private String category;
    @JsonProperty("fine_tuning")
    private FineTuningDTO fine_tuning;
    @JsonProperty("labels")
    private LabelsDTO labels;
    @JsonProperty("description")
    private String description;
    @JsonProperty("preview_url")
    private String preview_url;
    @JsonProperty("available_for_tiers")
    private List<String> available_for_tiers;
    @JsonProperty("settings")
    private Object settings;
    @JsonProperty("sharing")
    private Object sharing;
    @JsonProperty("high_quality_base_model_ids")
    private List<String> high_quality_base_model_ids;
    @JsonProperty("safety_control")
    private Object safety_control;
    @JsonProperty("voice_verification")
    private VoiceVerificationDTO voice_verification;
    @JsonProperty("permission_on_resource")
    private Object permission_on_resource;
    @JsonProperty("is_legacy")
    private boolean is_legacy;
    @JsonProperty("is_mixed")
    private boolean is_mixed;

}
