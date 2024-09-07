package com.globo.upcomingAds.controllers;

import com.globo.upcomingAds.enums.AnnouncerEnum;
import com.globo.upcomingAds.enums.TemplateAudioEnum;
import com.globo.upcomingAds.enums.TemplateVideoEnum;
import com.globo.upcomingAds.services.AudioService;
import com.globo.upcomingAds.services.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("video")
public class VideoController {

    public static final String LOGO_PATH = "C:\\hack\\automatizacao\\logo.mp4";
    public static final String OUTPUT_PATH = "C:\\hack\\automatizacao\\output.mp4";
    public static final String SOUNDTRACK_OUTPUT = "C:\\hack\\automatizacao\\trilhaSonoraOutput.mp3";
    public static final String AUDIO_TREATED = "C:\\hack\\automatizacao\\locucaoOutput.wav";
    public static final String AUDIO_TREATED_2 = "C:\\hack\\automatizacao\\audio_output.mp3";
    public static final String AUDIO_PATH = "C:\\hack\\automatizacao\\locucao.wav";


    private final AudioService audioService;
    private final VideoService videoService;

    public VideoController(AudioService audioService, VideoService videoService) {
        this.audioService = audioService;
        this.videoService = videoService;
    }

    @GetMapping("/create-video-voiceover-delivered")
    public ResponseEntity<String> createVideoVoiceoverDelivered(@RequestParam final TemplateVideoEnum videoEnum,
                                                               @RequestParam final TemplateAudioEnum audioEnum) throws Exception {

        audioService.removeSilence(AUDIO_PATH, AUDIO_TREATED);
        if (videoService.checkIfAudioFitsVideo(videoEnum.getId(), AUDIO_TREATED)) {
            audioService.createSoundTrack(audioEnum.getId(), videoEnum.getId(), SOUNDTRACK_OUTPUT);
            return ResponseEntity.ok().body(videoService.mergeVideoAudio(videoEnum.getId(),
                    AUDIO_TREATED, OUTPUT_PATH, SOUNDTRACK_OUTPUT, LOGO_PATH));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Seu Audio tem o tamanho maior que o vídeo, por favor envie uma locução com duração adequada");
        }
    }

    @PostMapping("/create-video-and-voiceover")
    public ResponseEntity<String> createVideoAndVoiceover(@RequestParam final TemplateVideoEnum videoEnum,
                                @RequestParam final TemplateAudioEnum audioEnum,
                                @RequestParam final AnnouncerEnum voiceId,
                                @RequestBody String text) throws Exception {

        audioService.convertTextToSpeech(voiceId.getId(), text);
        audioService.createSoundTrack(audioEnum.getId(), videoEnum.getId(), SOUNDTRACK_OUTPUT);
        return ResponseEntity.ok().body(videoService.mergeVideoAudio(videoEnum.getId(), AUDIO_TREATED_2, OUTPUT_PATH, SOUNDTRACK_OUTPUT, LOGO_PATH));

    }


}
