package com.globo.upcomingAds.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModelIdEnum {

    ELEVEN_TURBO_V_2_5("eleven_turbo_v2_5"),
    ELEVEN_TURBO_V2("eleven_turbo_v2"),
    ELEVEN_MULTILINGUAL_STS_V2("eleven_multilingual_sts_v2"),
    ELEVEN_MONOLINGUAL_V1("eleven_monolingual_v1"),
    ELEVEN_ENGLISH_STS_V2("eleven_english_sts_v2"),
    ELEVEN_MULTILINGUAL_V1("eleven_multilingual_v1"),
    ELEVEN_MULTILINGUAL_V2("eleven_multilingual_v2");

    private String id;
}
