package com.globo.upcomingAds.services;

import com.globo.upcomingAds.client.ElevenLabsClient;
import com.globo.upcomingAds.dtos.VideoRequest;
import com.globo.upcomingAds.dtos.request.AudioRequestDTO;
import com.globo.upcomingAds.dtos.request.VoiceSettingsRequestDTO;
import com.globo.upcomingAds.enums.ModelIdEnum;
import com.globo.upcomingAds.enums.TemplateAudioEnum;
import com.globo.upcomingAds.enums.TemplateVideoEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class VideoService {


    private final ElevenLabsClient elevenLabsClient;
    private final AudioService audioService;


    public VideoService(ElevenLabsClient elevenLabsClient, AudioService audioService) {
        this.elevenLabsClient = elevenLabsClient;
        this.audioService = audioService;
    }

    public static final String PT = "pt";


    public String mergeVideoAudio(String videoPath, String audioTreated, String outputPath, String soundtrackOutput, String logoPath) {
        try {

            double videoDuration = getMediaDuration(videoPath);
            double audioDuration1 = getMediaDuration(audioTreated);

            double startDelaySeconds = (videoDuration - audioDuration1) / 2;
            int startDelayMillis = (int) (startDelaySeconds * 1000);

            /* ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoPath,
                    "-i", audioTreated,
                    "-i", soundtrackOutput,
                    "-i", logoPath,
                    "-filter_complex",
                    "[1:a]adelay=" + startDelayMillis + "|"+ startDelayMillis +"[a1];" +  // Adiciona atraso para centralizar o áudio
                            "[2:a]volume=-8dB[a2];" +  // Ajusta o volume da trilha sonora
                            "[a1][a2]amix=inputs=2:duration=longest:dropout_transition=3[aout];" + // Combina os áudios mantendo a duração do áudio mais longo
                            "[3:v]chromakey=0x00FF00:0.1:0.2,scale=iw*0.2:ih*0.2[logo_transparente];" +  // Remove fundo verde e redimensiona o logo em 80%
                            "[0:v][logo_transparente]overlay=10:H-h-10[vout]",  // Posiciona o logo no canto inferior esquerdo
                    "-map", "[vout]",
                    "-map", "[aout]",
                    "-c:v", "libx264",
                    "-c:a", "aac",
                    "-strict", "experimental",
                    outputPath
            ); */

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoPath,
                    "-i", audioTreated,
                    "-i", soundtrackOutput,
                    "-i", logoPath,
                    "-filter_complex",
                    "[1:a]volume=4dB,adelay=" + startDelayMillis + "|" + startDelayMillis + "[a1];" +  // Aumenta o volume do áudio tratado
                            "[2:a]volume=-15dB[a2];" +  // Diminui um pouco mais a trilha sonora
                            "[a1][a2]amix=inputs=2:duration=longest:dropout_transition=3[aout];" +
                            "[3:v]scale=100:-1[logo_scaled];" +
                            "[0:v][logo_scaled]overlay=10:main_h-overlay_h-10[vout]",
                    "-map", "[vout]",
                    "-map", "[aout]",
                    "-c:v", "libx264",
                    "-c:a", "aac",
                    "-strict", "experimental",
                    outputPath
            );

            pb.redirectErrorStream(true); // Redireciona o erro padrão para a saída padrão
            Process process = pb.start();

            // Cria uma thread para consumir a saída do processo
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line); // Ou registre em um log
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            outputReader.start(); // Inicia a leitura da saída do processo
            process.waitFor();

            if (process.exitValue() != 0) {
                throw new RuntimeException("Erro ao fazer merge video e áudios.");
            }

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

    public ResponseEntity<Map<String, Object>> createVideo(VideoRequest request) {

        try {
            // Validar os dados recebidos
            if (request.texto() == null || request.texto().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Texto da locução não pode estar vazio"
                ));
            }

            // Gerar um ID único para o vídeo
            String videoId = UUID.randomUUID().toString();

            // Definir caminhos para os arquivos
            String baseDir = "C:\\hack\\automatizacao\\";
            String tempDir = baseDir + "temp\\" + videoId + "\\";
            String outputDir = baseDir + "output\\";
            String outputPath = outputDir + videoId + ".mp4";

            // Criar diretório temporário
            new File(tempDir).mkdirs();
            new File(outputDir).mkdirs();

            log.info("Diretório temporário criado: {}", tempDir);
            log.info("Diretório de saída criado: {}", outputDir);

            // Caminhos dos arquivos
            String audioPath = tempDir + "audio.mp3";
            String audioTreated = tempDir + "audio_treated.mp3";
            String soundtrackOutput = tempDir + "soundtrack_output.mp3";
            String logoPath = tempDir + "logo.png";

            // Determinar qual template de vídeo usar
            String videoPath;
            if (request.videoPath() != null && !request.videoPath().isEmpty()) {
                // Mapear o caminho relativo para o caminho absoluto
                videoPath = baseDir + request.videoPath().replace("/", "\\").substring(1);
            } else {
                // Usar template padrão
                videoPath = TemplateVideoEnum.TEMPLATE_O1.getId();
            }

            // Determinar qual trilha sonora usar
            String musicPath;
            if (request.musicaCaminho() != null && !request.musicaCaminho().isEmpty()) {
                // Mapear o caminho relativo para o caminho absoluto
                musicPath = baseDir + request.musicaCaminho().replace("/", "\\").substring(1);
            } else {
                // Usar trilha sonora padrão
                musicPath = TemplateAudioEnum.TEMPLATE_O1.getId();
            }

            logoPath = saveLogo(request, logoPath, baseDir);

            // Gerar o arquivo de áudio a partir do texto usando ElevenLabs
            boolean audioGenerated = generateAudioWithElevenLabs(request.texto(), request.locutorUuid(), audioPath);

            if (!audioGenerated) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "success", false,
                        "message", "Falha ao gerar áudio a partir do texto usando ElevenLabs"
                ));
            }

            // Processar o áudio e criar o vídeo
            audioService.removeSilence(audioPath, audioTreated);

            if (checkIfAudioFitsVideo(videoPath, audioTreated)) {
                audioService.createSoundTrack(musicPath, videoPath, soundtrackOutput);

                // Verificar se todos os arquivos de entrada existem antes de chamar o merge
                File audioFile = new File(audioTreated);
                File videoFile = new File(videoPath);
                File soundtrackFile = new File(soundtrackOutput);
                File logoFile = new File(logoPath);

                if (!audioFile.exists()) {
                    throw new FileNotFoundException("Arquivo de áudio tratado não encontrado: " + audioTreated);
                }
                if (!videoFile.exists()) {
                    throw new FileNotFoundException("Arquivo de vídeo não encontrado: " + videoPath);
                }
                if (!soundtrackFile.exists()) {
                    throw new FileNotFoundException("Arquivo de trilha sonora não encontrado: " + soundtrackOutput);
                }
                if (!logoFile.exists()) {
                    throw new FileNotFoundException("Arquivo de logo não encontrado: " + logoPath);
                }

                String result = mergeVideoAudio(videoPath, audioTreated, outputPath, soundtrackOutput, logoPath);

                // Verificar se o arquivo de saída foi criado
                File outputFile = new File(outputPath);
                if (!outputFile.exists()) {
                    throw new FileNotFoundException("Arquivo de saída não foi criado: " + outputPath);
                }

                // Retornar sucesso com o ID do vídeo
                return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", result,
                        "videoId", videoId,
                        "videoUrl", "/videos/" + videoId + ".mp4"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "success", false,
                        "message", "Seu áudio tem o tamanho maior que o vídeo, por favor envie uma locução com duração adequada"
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erro ao criar vídeo: " + e.getMessage()
            ));
        }
    }


    private String saveLogo(VideoRequest request, String logoPath, String baseDir) throws IOException {
        // Salvar o logo
        if (request.logo() != null && !request.logo().isEmpty()) {
            // Se o logo for uma string base64
            if (request.logo().startsWith("data:")) {
                request.saveBase64AsFile(request.logo(), logoPath);
            } else {
                // Se for um caminho de arquivo
                // Copiar o arquivo para o diretório temporário
                Files.copy(Paths.get(baseDir + request.logo()), Paths.get(logoPath));
            }
        } else {
            // Usar um logo padrão
            logoPath = baseDir + "default_logo.png";
        }
        return logoPath;
    }


    private boolean generateAudioWithElevenLabs(String text, String voiceId, String outputPath) {
        try {
            // Valores padrão para os parâmetros
            Double stability = 0.5;
            Double similarityBoost = 0.5;
            Double style = 1.0;

            // Validar e ajustar parâmetros conforme necessário
            Map<String, Double> params = validateInputs(voiceId, text, stability, similarityBoost, style);

            // Buscar o áudio da API
            byte[] audioBytes = fetchAudioFromAPI(voiceId, text, params.get("stability"),
                    params.get("similarityBoost"), params.get("style"));

            if (audioBytes == null || audioBytes.length == 0) {
                log.error("Falha ao obter áudio da API ElevenLabs");
                return false;
            }

            // Salvar o arquivo de áudio no caminho especificado
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(audioBytes);
                log.info("Arquivo de áudio salvo com sucesso em: {}", outputPath);
                return true;
            } catch (IOException e) {
                log.error("Erro ao salvar arquivo de áudio: {}", e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            log.error("Erro ao gerar áudio com ElevenLabs: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, Double> validateInputs(String voiceId, String text, Double stability,
                                               Double similarityBoost, Double style) {
        Map<String, Double> params = new HashMap<>();

        // Validar voiceId
        if (voiceId == null || voiceId.isEmpty()) {
            throw new IllegalArgumentException("VoiceId não pode ser nulo ou vazio");
        }

        // Validar texto
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Texto não pode ser nulo ou vazio");
        }

        // Validar e ajustar stability
        if (stability == null || stability < 0 || stability > 1) {
            stability = 0.5; // Valor padrão
        }
        params.put("stability", stability);

        // Validar e ajustar similarityBoost
        if (similarityBoost == null || similarityBoost < 0 || similarityBoost > 1) {
            similarityBoost = 0.5; // Valor padrão
        }
        params.put("similarityBoost", similarityBoost);

        // Validar e ajustar style
        if (style == null || style < 0 || style > 1) {
            style = 0.0; // Valor padrão
        }
        params.put("style", style);

        return params;
    }

    private byte[] fetchAudioFromAPI(String voiceId, String text, Double stability,
                                     Double similarityBoost, Double style) {
        try {
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

            return elevenLabsClient.convertTextToSpeechStream(voiceId, request);
        } catch (Exception e) {
            log.error("Erro ao buscar áudio da API ElevenLabs: {}", e.getMessage());
            e.printStackTrace();
            return new byte[0];
        }
    }
}
