package com.globo.upcomingAds.services;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AudioService {

    public String removeSilence(String audioPath, String audioTreated) throws IOException, InterruptedException {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", audioPath,
                    "-af", "silenceremove=start_periods=1:start_silence=0.5:start_threshold=-50dB:detection=peak,"
                    + "areverse,silenceremove=start_periods=1:start_silence=0.5:start_threshold=-50dB:detection=peak,areverse",
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
