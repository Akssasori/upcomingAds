package com.globo.upcomingAds.services;

import com.globo.upcomingAds.utils.MediaUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

            pb.redirectErrorStream(true);  // Redireciona o erro padrão para a saída padrão
            Process process = pb.start();

            // Consumir a saída para evitar bloqueio
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // registrar em um log?
                }
            }

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

    public void createSoundTrack(String soundtrack, String videoPath, String soundtrackOutput) throws Exception {
        double mediaDuration = MediaUtils.getMediaDuration(videoPath);
        trimSoundtrack(Double.toString(mediaDuration), soundtrack, soundtrackOutput);

    }

    private void trimSoundtrack(String mediaDuration, String soundtrack, String soundtrackOutput) throws Exception {

        double soundTrackDuration = MediaUtils.getMediaDuration(soundtrack);

        if (soundTrackDuration < Double.parseDouble(mediaDuration)) {
            throw new RuntimeException("Erro, trilha sonora tem um tempo menor que o vídeo.");
        }

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", soundtrack,
                "-ss", "10", // Define a marcação inicial para cortar o áudio
                "-t", mediaDuration, // Define a duração do recorte
                "-c:a", "libmp3lame",
                soundtrackOutput
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Consumir a saída para evitar bloqueio
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        process.waitFor();

        if (process.exitValue() != 0) {
            throw new RuntimeException("Erro ao recortar áudio.");
        }

    }
}
