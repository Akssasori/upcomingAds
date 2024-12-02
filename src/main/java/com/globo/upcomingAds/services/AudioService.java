package com.globo.upcomingAds.services;

import com.globo.upcomingAds.client.ElevenLabsClient;
import com.globo.upcomingAds.dtos.request.AudioRequestDTO;
import com.globo.upcomingAds.dtos.request.VoiceSettingsRequestDTO;
import com.globo.upcomingAds.enums.ModelIdEnum;
import com.globo.upcomingAds.utils.MediaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@Slf4j
public class AudioService {

    public static final String OUTPUT_FORMAT = "mp3_22050_32";
    public static final String ELEVEN_TURBO_V_2_5 = "eleven_turbo_v2_5";
    public static final String PT = "pt";
    public static final String OUTPUT_PATH = "C:\\hack\\automatizacao\\audio_output.mp3";

    private final WebClient webClient;
    private final ElevenLabsClient elevenLabsClient;

    public AudioService(WebClient webClient, ElevenLabsClient elevenLabsClient) {
        this.webClient = webClient;
        this.elevenLabsClient = elevenLabsClient;
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

    public String createSoundTrack(String soundtrack, String videoPath, String soundtrackOutput) throws Exception {
        double mediaDuration = MediaUtils.getMediaDuration(videoPath);
        return trimSoundtrack(Double.toString(mediaDuration), soundtrack, soundtrackOutput);

    }

    private String trimSoundtrack(String mediaDuration, String soundtrack, String soundtrackOutput) throws Exception {

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
        return "Trilha criada com sucesso";
    }

    public InputStream convertTextToSpeech(String voiceId, String text) {

        try {

            byte[] audioBytes = elevenLabsClient.convertTextToSpeech(voiceId, AudioRequestDTO.builder()
                    .modelId(ModelIdEnum.ELEVEN_TURBO_V_2_5.getId())
                    .text(text)
                    .voiceSettings(VoiceSettingsRequestDTO.builder()
                            .stability(0.4)
                            .similarityBoost(0.6)
                            .build())
                    .build());

            InputStream inputStream = new ByteArrayInputStream(audioBytes);

            Path outputPath = Paths.get(OUTPUT_PATH);
            File outputFile = outputPath.toFile();

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
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
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Erro na requisição: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException(e.getMessage());
        }

    }

    public String getVoice(String voiceId) {

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/voices/" + voiceId)
//                            .queryParam("show_legacy", voiceId)
                            .build())
//                    .header("xi-api-key", API_KEY)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Erro na requisição: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String createSpeaker(MultipartFile[] multipartFiles, String name) {

        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri("/v1/voices/add")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("name", name)
                        .with("description", "Gerente")  // Descrição fixa, pode ajustar conforme necessário
                        .with("files", multipartFiles[0].getResource()))  // Supondo que você envie um arquivo. Ajuste para múltiplos arquivos se necessário
                .retrieve();

        // Processar a resposta da API externa
        Mono<String> responseBody = responseSpec.bodyToMono(String.class);

        return responseBody.block();
    }

    public String convertTextToSpeechStream(String voiceId, String text, Double stability,
                                            Double similarityBoost, Double style) {

        validateInputs(voiceId, text, stability, similarityBoost, style);

        byte[] audioBytes = fetchAudioFromAPI(voiceId, text, stability, similarityBoost, style);

        saveAudioFile(audioBytes);

        return "Audio created successfully";
    }

    private void saveAudioFile(byte[] audioBytes) {
        File outputFile = Paths.get(OUTPUT_PATH).toFile();

        try (InputStream inputStream = new ByteArrayInputStream(audioBytes);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }

            log.info("Audio file saved successfully at: {}", OUTPUT_PATH);
        } catch (IOException e) {
            log.error("Error saving audio file", e);
            throw new RuntimeException("Failed to save audio file", e);
        }
    }

    private byte[] fetchAudioFromAPI(String voiceId, String text, Double stability,
                                     Double similarityBoost, Double style) {
        VoiceSettingsRequestDTO voiceSettings = VoiceSettingsRequestDTO.builder()
                .stability(stability)
                .similarityBoost(similarityBoost)
                .style(style)
                .build();

        AudioRequestDTO request = AudioRequestDTO.builder()
                .modelId(ModelIdEnum.ELEVEN_TURBO_V_2_5.getId())
                .text(text)
                .languageCode(PT)
                .voiceSettings(voiceSettings)
                .build();

        return elevenLabsClient.convertTextToSpeechStream(voiceId, request);
    }

    private void validateInputs(String voiceId, String text, Double stability, Double similarityBoost, Double style) {
        double defaultStability = 0.4;
        double defaultSimilarityBoost = 0.6;
        double defaultStyle = 0.9;

        if (Objects.isNull(voiceId) || voiceId.isBlank()) {
            throw new IllegalArgumentException("Voice ID cannot be null or empty");
        }
        if (Objects.isNull(text) || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        if (Objects.isNull(stability) || stability < 0 || stability > 1) {
            stability = defaultStability;
        }
        if (Objects.isNull(similarityBoost) || similarityBoost < 0 || similarityBoost > 1) {
            similarityBoost = defaultSimilarityBoost;
        }
        if (Objects.isNull(style) || style < 0 || style > 1) {
            style = defaultStyle;
        }
    }

}

