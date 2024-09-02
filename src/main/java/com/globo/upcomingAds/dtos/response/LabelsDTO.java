package com.globo.upcomingAds.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelsDTO {

    @JsonProperty("accent")
    private String accent;
    @JsonProperty("description")
    private String description;
    @JsonProperty("age")
    private String age;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("use_case")
    private String use_case;

}
