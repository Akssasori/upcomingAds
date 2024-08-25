package com.globo.upcomingAds.controllers;

//import org.bytedeco.ffmpeg.global.avfilter;
//import org.bytedeco.ffmpeg.global.avformat;
//import org.bytedeco.ffmpeg.global.avcodec;
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

    @GetMapping("/merge-video-audio")
    public String mergeVideoWithAudio() throws IOException, InterruptedException {
        // Inicializar as bibliotecas do FFmpeg
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        swscale.sws_getContext(0, 0, 0, 0, 0, 0, 0, new SwsFilter(), new SwsFilter(),new DoublePointer());

        String videoPath = "C:\\hack\\automatizacao\\template.mp4";
        String audioPath = "C:\\hack\\automatizacao\\locucao.wav";
        String audioTreated = "C:\\hack\\automatizacao\\locucaoOutput.wav";
        String outputPath = "C:\\hack\\automatizacao\\output.mp4";

        removeSilence(audioPath,audioTreated);

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoPath,
                    "-i", audioPath,
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-strict", "experimental",
                    outputPath
            );

            Process process = pb.start();
            process.waitFor();

            return "Vídeo criado com sucesso: " + outputPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao criar vídeo: " + e.getMessage();
        }
    }

    private String removeSilence(String audioPath, String audioTreated) throws IOException, InterruptedException {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", audioPath,
                    "-af", "silenceremove=start_periods=1:start_silence=0.5:start_threshold=-50dB:detection=peak,"
                    + "reverse,silenceremove=start_periods=1:start_silence=0.5:start_threshold=-50dB:detection=peak,reverse",
                    audioTreated
            );

            Process process = pb.start();
            process.waitFor();

            if (process.exitValue() == 0) {
                return "Áudio processado com sucesso: " + audioTreated;
            } else {
                return "Erro ao processar áudio.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao tratar o audio: " + e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Erro ao tratar o audio: " + e.getMessage();
        }
    }


}
