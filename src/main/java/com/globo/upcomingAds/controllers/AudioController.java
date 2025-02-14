package com.globo.upcomingAds.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globo.upcomingAds.dtos.response.VoiceDTO;
import com.globo.upcomingAds.dtos.response.VoiceIdDTO;
import com.globo.upcomingAds.dtos.response.VoiceResponseDTO;
import com.globo.upcomingAds.enums.AnnouncerEnum;
import com.globo.upcomingAds.enums.TemplateVideoEnum;
import com.globo.upcomingAds.services.AudioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("audio")
public class AudioController {

    public static final String SOUNDTRACK = "C:\\hack\\automatizacao\\trilhaSonora.mp3";
    public static final String SOUNDTRACK_OUTPUT = "C:\\hack\\automatizacao\\trilhaSonoraOutput.mp3";

    @Value("${api.elevenlabs.key}")
    private String apiKey;

    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        audioService.getMoldes();
        return ResponseEntity.ok().body("ok");
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

    @PostMapping("/convert-text-to-speech-stream")
    public ResponseEntity<String> convertTextToSpeechStream(@RequestParam final AnnouncerEnum voiceId,
                                                            @RequestParam(required = false) final Double stability,
                                                            @RequestParam(required = false) final Double similarityBoost,
                                                            @RequestParam(required = false) final Double style,
                                                            @RequestBody String text) {

        return ResponseEntity.ok(audioService.convertTextToSpeechStream(voiceId.getId(),
                text, stability, similarityBoost, style));

    }

    @GetMapping("get-voices/{show_legacy}")
    public ResponseEntity<VoiceResponseDTO> getVoices(@PathVariable boolean show_legacy) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String voices = audioService.getVoices(show_legacy);
        VoiceResponseDTO voiceResponseDTO = objectMapper.readValue(voices, VoiceResponseDTO.class);
        voiceResponseDTO.getVoices().forEach(voiceDTO -> {
            System.out.println(voiceDTO.getVoice_id() + " " + voiceDTO.getName());
        });
        return ResponseEntity.ok(voiceResponseDTO);
    }

    @GetMapping("get-voice/{voice_id}")
    public ResponseEntity<VoiceDTO> getVoice(@PathVariable String voice_id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String voices = audioService.getVoice(voice_id);
        VoiceDTO voiceDTO = objectMapper.readValue(voices, VoiceDTO.class);
        return ResponseEntity.ok(voiceDTO);
    }

    @PostMapping("/create-soundtrack-for-video")
    public ResponseEntity<String> createSoundtrack(@RequestParam final TemplateVideoEnum videoEnum) throws Exception {
        return ResponseEntity.ok().body(audioService.createSoundTrack(SOUNDTRACK, videoEnum.getId(), SOUNDTRACK_OUTPUT));
    }

    @PostMapping(value = "/create-speaker/{name}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<VoiceIdDTO> createSpeaker(@RequestPart("files") MultipartFile[] multipartFiles,
                                                    @PathVariable(required = true) String name,
                                                    @RequestParam(required = true) String description,
                                                    @RequestParam(required = true) boolean removeBackgroundNoise) {

        String voiceId = audioService.createSpeaker(multipartFiles, name.toUpperCase(), description, removeBackgroundNoise);
        return ResponseEntity.ok().body(VoiceIdDTO.builder().voiceId(voiceId).build());
    }


}
