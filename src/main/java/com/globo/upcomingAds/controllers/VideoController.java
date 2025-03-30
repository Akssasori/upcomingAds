package com.globo.upcomingAds.controllers;

import com.globo.upcomingAds.client.ElevenLabsClient;
import com.globo.upcomingAds.dtos.VideoRequest;
import com.globo.upcomingAds.dtos.request.AudioRequestDTO;
import com.globo.upcomingAds.dtos.request.VoiceSettingsRequestDTO;
import com.globo.upcomingAds.dtos.response.LabelsDTO;
import com.globo.upcomingAds.enums.AnnouncerEnum;
import com.globo.upcomingAds.enums.ModelIdEnum;
import com.globo.upcomingAds.enums.TemplateAudioEnum;
import com.globo.upcomingAds.enums.TemplateVideoEnum;
import com.globo.upcomingAds.mappers.TestMapper;
import com.globo.upcomingAds.services.AudioService;
import com.globo.upcomingAds.services.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("video")
@Slf4j
public class VideoController {

    public static final String LOGO_PATH = "C:\\hack\\automatizacao\\logo.mp4";
    public static final String OUTPUT_PATH = "C:\\hack\\automatizacao\\output.mp4";
    public static final String SOUNDTRACK_OUTPUT = "C:\\hack\\automatizacao\\trilhaSonoraOutput.mp3";
    public static final String AUDIO_TREATED = "C:\\hack\\automatizacao\\locucaoOutput.wav";
    public static final String AUDIO_TREATED_2 = "C:\\hack\\automatizacao\\audio_output.mp3";
    public static final String AUDIO_PATH = "C:\\hack\\automatizacao\\locucao.wav";


    private final AudioService audioService;
    private final VideoService videoService;
    private final ElevenLabsClient elevenLabsClient;

    public VideoController(AudioService audioService, VideoService videoService, ElevenLabsClient elevenLabsClient) {
        this.audioService = audioService;
        this.videoService = videoService;
        this.elevenLabsClient = elevenLabsClient;
    }

    @GetMapping("/create-video-voiceover-delivered")
    public ResponseEntity<String> createVideoVoiceoverDelivered(@RequestParam final TemplateVideoEnum videoEnum,
                                                                @RequestParam final TemplateAudioEnum audioEnum) throws Exception {

        audioService.removeSilence(AUDIO_PATH, AUDIO_TREATED);
        if (videoService.checkIfAudioFitsVideo(videoEnum.getId(), AUDIO_TREATED)) {
            audioService.createSoundTrack(audioEnum.getId(), videoEnum.getId(), SOUNDTRACK_OUTPUT);
            return ResponseEntity.ok().body(videoService.mergeVideoAudio(videoEnum.getId(),
                    AUDIO_TREATED, OUTPUT_PATH, SOUNDTRACK_OUTPUT, LOGO_PATH));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Seu Audio tem o tamanho maior que o vídeo, por favor envie uma locução com duração adequada");
        }
    }

    @PostMapping("/create-video-and-voiceover")
    public ResponseEntity<String> createVideoAndVoiceover(@RequestParam final TemplateVideoEnum videoEnum,
                                                          @RequestParam final TemplateAudioEnum audioEnum,
                                                          @RequestParam final AnnouncerEnum voiceId,
                                                          @RequestBody String text) throws Exception {

        audioService.convertTextToSpeech(voiceId.getId(), text);
        audioService.createSoundTrack(audioEnum.getId(), videoEnum.getId(), SOUNDTRACK_OUTPUT);
        return ResponseEntity.ok().body(videoService.mergeVideoAudio(videoEnum.getId(), AUDIO_TREATED_2, OUTPUT_PATH, SOUNDTRACK_OUTPUT, LOGO_PATH));

    }

    @PostMapping("/create-video")
    public ResponseEntity<Map<String, Object>> createVideo(@RequestBody VideoRequest request) {
        log.info("Recebendo solicitação para criar vídeo com texto: {}", request.texto());

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

            if (videoService.checkIfAudioFitsVideo(videoPath, audioTreated)) {
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

                String result = videoService.mergeVideoAudio(videoPath, audioTreated, outputPath, soundtrackOutput, logoPath);

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

    // Metodo para gerar áudio usando ElevenLabs
    private boolean generateAudioWithElevenLabs(String text, String voiceId, String outputPath) {
        try {
            // Valores padrão para os parâmetros
            Double stability = 0.5;      // Valor padrão para estabilidade
            Double similarityBoost = 0.5; // Valor padrão para similarityBoost
            Double style = 0.0;          // Valor padrão para style

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

    // Método para validar os inputs (reutilizando seu código existente)
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

    // Método para buscar áudio da API (reutilizando seu código existente)
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
                    .languageCode("pt") // Assumindo que PT é uma constante para "pt"
                    .voiceSettings(voiceSettings)
                    .build();

            // Chamar a API da ElevenLabs
            byte[] bytesAudio = elevenLabsClient.convertTextToSpeechStream(voiceId, request);
            return bytesAudio;
        } catch (Exception e) {
            log.error("Erro ao buscar áudio da API ElevenLabs: {}", e.getMessage());
            e.printStackTrace();
            return new byte[0];
        }
    }
}
