package com.globo.upcomingAds.services;

import com.globo.upcomingAds.client.ElevenLabsClient;
import com.globo.upcomingAds.dtos.request.AudioRequestDTO;
import com.globo.upcomingAds.dtos.request.VoiceSettingsRequestDTO;
import com.globo.upcomingAds.dtos.response.VoiceIdDTO;
import com.globo.upcomingAds.enums.ModelIdEnum;
import com.globo.upcomingAds.utils.MediaUtils;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class AudioService {

    public static final String OUTPUT_FORMAT = "mp3_22050_32";
    public static final String ELEVEN_TURBO_V_2_5 = "eleven_turbo_v2_5";
    public static final String PT = "pt";
    public static final String OUTPUT_PATH = "C:\\hack\\automatizacao\\audio_output.mp3";

    private final ElevenLabsClient elevenLabsClient;

    public AudioService(ElevenLabsClient elevenLabsClient) {
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
            return elevenLabsClient.getVoices(showLegacy);
        } catch (FeignException e) {
            log.error("Erro na requisição: " + e.status() + " - " + e.contentUTF8());
            throw new RuntimeException(e.getMessage());
        }

    }

    public String getVoice(String voiceId) {

        try {
            return elevenLabsClient.getVoice(voiceId);
        } catch (FeignException e) {
            log.error("Erro na requisição: " + e.status() + " - " + e.contentUTF8());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String createSpeaker(MultipartFile[] multipartFiles, String name, String description, boolean removeBackgroundNoise) {

        if (multipartFiles == null || multipartFiles.length == 0) {
            throw new IllegalArgumentException("No files provided for upload.");
        }

        List<MultipartFile> files = Arrays.asList(multipartFiles);

        ResponseEntity<VoiceIdDTO> voiceIdDTOResponseEntity = elevenLabsClient.addVoice(files, name, removeBackgroundNoise, description);

        return voiceIdDTOResponseEntity.getBody().getVoiceId();
    }

    public String convertTextToSpeechStream(String voiceId, String text, Double stability,
                                            Double similarityBoost, Double style) {

        log.info("Iniciando validação dos inputs");
        Map<String, Double> params = validateInputs(voiceId, text, stability, similarityBoost, style);

        log.info("Iniciando criação da locução");
        byte[] audioBytes = fetchAudioFromAPI(voiceId, text, params.get("stability"),
                params.get("similarityBoost"), params.get("style"));

        log.info("Salvando arquivo .mp3");
        saveAudioFile(audioBytes);

        return "Audio created successfully " + OUTPUT_PATH;
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
                .speed(1)
                .style(style)
                .use_speaker_boos(true)
                .build();

        AudioRequestDTO request = AudioRequestDTO.builder()
                .modelId(ModelIdEnum.ELEVEN_TURBO_V_2_5.getId())
                .text(text)
                .languageCode(PT)
                .voiceSettings(voiceSettings)
                .build();

        try {
            elevenLabsClient.convertTextToSpeechStream(voiceId, request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return elevenLabsClient.convertTextToSpeechStream(voiceId, request);
    }

    private Map<String, Double> validateInputs(String voiceId, String text, Double stability, Double similarityBoost, Double style) {

        if (Objects.isNull(voiceId) || voiceId.isBlank()) {
            throw new IllegalArgumentException("Voice ID cannot be null or empty");
        }
        if (Objects.isNull(text) || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        double defaultStability = 0.4;
        double defaultSimilarityBoost = 0.6;
        double defaultStyle = 0.9;

        stability = (stability == null || stability < 0 || stability > 1) ? defaultStability : stability;
        similarityBoost = (similarityBoost == null || similarityBoost < 0 || similarityBoost > 1) ? defaultSimilarityBoost : similarityBoost;
        style = (style == null || style < 0 || style > 1) ? defaultStyle : style;

        Map<String, Double> params = new HashMap<>();

        params.put("stability", stability);
        params.put("similarityBoost", similarityBoost);
        params.put("style", style);

        return params;
    }

    public void getMoldes() {
        elevenLabsClient.getModels();
    }
}

