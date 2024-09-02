package com.globo.upcomingAds.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VoiceResponseDTO {
    private List<VoiceDTO> voices;
}
