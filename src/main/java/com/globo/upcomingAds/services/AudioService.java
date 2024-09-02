package com.globo.upcomingAds.services;

import com.globo.upcomingAds.utils.MediaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.io.*;

@Service
public class AudioService {

    @Value("${api.elevenlabs.key}")
    private String API_KEY;

    private final WebClient webClient;

    public AudioService(WebClient webClient) {
        this.webClient = webClient;
    }

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

    public InputStream convertTextToSpeech(String voiceId, String text) {

        try {
            byte[] audioBytes = webClient.post()
                    .uri("/v1/text-to-speech/" + voiceId)
                    .header("xi-api-key", API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{\"text\":\"" + text + "\"}")
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            return new ByteArrayInputStream(audioBytes);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public String getVoices(boolean showLegacy) {

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/voices")
                            .queryParam("show_legacy", showLegacy)
                            .build())
                    .header("xi-api-key", API_KEY)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Erro na requisição: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException(e.getMessage());
        }

    }
}
