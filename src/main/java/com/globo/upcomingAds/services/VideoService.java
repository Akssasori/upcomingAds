package com.globo.upcomingAds.services;

import org.springframework.stereotype.Service;

@Service
public class VideoService {

    public String mergeVideoAudio(String videoPath, String audioTreated, String outputPath) {
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
}
