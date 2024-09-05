package com.globo.upcomingAds.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globo.upcomingAds.dtos.response.VoiceDTO;
import com.globo.upcomingAds.dtos.response.VoiceResponseDTO;
import com.globo.upcomingAds.enums.AnnouncerEnum;
import com.globo.upcomingAds.services.AudioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("audio")
public class AudioController {

    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @PostMapping("/convert-text-to-speech")
    public ResponseEntity<String> convertTextToSpeech(@RequestParam final AnnouncerEnum voiceId,
                                                      @RequestBody String text) {

    try {
        audioService.convertTextToSpeech(voiceId.getId(), text);
        return ResponseEntity.ok("Áudio salvo com sucesso");

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao salvar o arquivo de áudio: " + e.getMessage());
    }

    }

    @GetMapping("get-voices/{show_legacy}")
    public ResponseEntity<VoiceResponseDTO> getVoices(@PathVariable boolean show_legacy) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String voices = audioService.getVoices(show_legacy);
        VoiceResponseDTO voiceResponseDTO  = objectMapper.readValue(voices, VoiceResponseDTO.class);
        voiceResponseDTO.getVoices().forEach(voiceDTO -> {
            System.out.println(voiceDTO.getVoice_id() + " " + voiceDTO.getName());
        });
        return ResponseEntity.ok(voiceResponseDTO);
    }

    @GetMapping("get-voice/{voice_id}")
    public ResponseEntity<VoiceDTO> getVoice(@PathVariable String voice_id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String voices = audioService.getVoice(voice_id);
        VoiceDTO voiceDTO  = objectMapper.readValue(voices, VoiceDTO.class);
        return ResponseEntity.ok(voiceDTO);
    }

}
