package com.globo.upcomingAds.controllers;

//import org.bytedeco.ffmpeg.global.avfilter;
//import org.bytedeco.ffmpeg.global.avformat;
//import org.bytedeco.ffmpeg.global.avcodec;
import com.globo.upcomingAds.services.AudioService;
import com.globo.upcomingAds.services.VideoService;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.ffmpeg.global.swscale;
import org.bytedeco.ffmpeg.swscale.SwsFilter;
import org.bytedeco.javacpp.DoublePointer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("video")
public class VideoController {

    public static final String LOGO_PATH = "C:\\hack\\automatizacao\\logo.mp4";
    public static final String OUTPUT_PATH = "C:\\hack\\automatizacao\\output.mp4";
    public static final String SOUNDTRACK_OUTPUT = "C:\\hack\\automatizacao\\trilhaSonoraOutput.mp3";
    public static final String SOUNDTRACK = "C:\\hack\\automatizacao\\trilhaSonora.mp3";
    public static final String AUDIO_TREATED = "C:\\hack\\automatizacao\\locucaoOutput.wav";
    public static final String AUDIO_PATH = "C:\\hack\\automatizacao\\locucao.wav";
    public static final String VIDEO_PATH = "C:\\hack\\automatizacao\\template.mp4";

    private final AudioService audioService;
    private final VideoService videoService;

    public VideoController(AudioService audioService, VideoService videoService) {
        this.audioService = audioService;
        this.videoService = videoService;
    }

    @GetMapping("/merge-video-audio")
    public String mergeVideoWithAudio() throws Exception {
        // Inicializar as bibliotecas do FFmpeg 6.1.1
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        swscale.sws_getContext(0, 0, 0, 0, 0, 0, 0, new SwsFilter(), new SwsFilter(),new DoublePointer());

        audioService.removeSilence(AUDIO_PATH,AUDIO_TREATED);
        audioService.createSoundTrack(SOUNDTRACK, VIDEO_PATH, SOUNDTRACK_OUTPUT);
        boolean isAudioShorterOrEqual = videoService.checkIfAudioFitsVideo(VIDEO_PATH, AUDIO_TREATED);
        System.out.println(isAudioShorterOrEqual);
        return videoService.mergeVideoAudio(VIDEO_PATH, AUDIO_TREATED, OUTPUT_PATH, SOUNDTRACK_OUTPUT, LOGO_PATH);

    }

    @GetMapping("/create-soundtrack")
    public void teste() throws Exception {
        // Inicializar as bibliotecas do FFmpeg 6.1.1
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        swscale.sws_getContext(0, 0, 0, 0, 0, 0, 0, new SwsFilter(), new SwsFilter(),new DoublePointer());

        audioService.createSoundTrack(SOUNDTRACK, VIDEO_PATH, SOUNDTRACK_OUTPUT);

    }



}
