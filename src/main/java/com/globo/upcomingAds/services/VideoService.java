package com.globo.upcomingAds.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class VideoService {

    public String mergeVideoAudio(String videoPath, String audioTreated, String outputPath, String soundtrackOutput) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoPath,
                    "-i", audioTreated,
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

    public boolean checkIfAudioFitsVideo(String videoPath, String audioTreated) throws Exception {
        double videoDuration = getMediaDuration(videoPath);
        double audioDuration = getMediaDuration(audioTreated);

        return audioDuration <= videoDuration;
    }

    public double getMediaDuration(String mediaPath) throws Exception {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "csv=p=0",
                    mediaPath
            );

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String durationStr = reader.readLine();

            process.waitFor();

            return Double.parseDouble(durationStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
