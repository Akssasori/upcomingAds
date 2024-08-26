package com.globo.upcomingAds.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class VideoService {

    public String mergeVideoAudio(String videoPath, String audioTreated, String outputPath, String soundtrackOutput, String logoPath) {
        try {
//            ProcessBuilder pb = new ProcessBuilder(
//                    "ffmpeg",
//                    "-i", videoPath,
//                    "-i", audioTreated,
//                    "-c:v", "copy",
//                    "-c:a", "aac",
//                    "-strict", "experimental",
//                    outputPath
//            );
//            ProcessBuilder pb = new ProcessBuilder(
//                    "ffmpeg",
//                    "-i", videoPath,
//                    "-i", audioTreated,
//                    "-i", soundtrackOutput,
//                    "-filter_complex",
//                    "[1:a]volume=1.0[a1];[2:a]volume=-8dB[a2];[a1][a2]amix=inputs=2:duration=first[aout]",
//                    "-map", "0:v",
//                    "-map", "[aout]",
//                    "-c:v", "copy",
//                    "-c:a", "aac",
//                    "-strict", "experimental",
//                    outputPath
//            );

//            ProcessBuilder pb = new ProcessBuilder(
//                    "ffmpeg",
//                    "-i", videoPath,
//                    "-i", audioTreated,
//                    "-i", soundtrackOutput,
//                    "-filter_complex",
//                    "[1:a]volume=1.0[a1];" +
//                            "[2:a]volume=-8dB,apad=pad_dur=0.1[a2];" +  // Adiciona uma pequena margem de silêncio
//                            "[a1][a2]amix=inputs=2:duration=first[aout]", // Usa a duração do vídeo para o áudio combinado
//                    "-map", "0:v",
//                    "-map", "[aout]",
//                    "-c:v", "copy",
//                    "-c:a", "aac",
//                    "-strict", "experimental",
//                    outputPath
//            );
//            ProcessBuilder pb = new ProcessBuilder(
//                    "ffmpeg",
//                    "-i", videoPath,
//                    "-i", audioTreated,
//                    "-i", soundtrackOutput,
//                    "-filter_complex",
//                    "[1:a]volume=1.0[a1];" +
//                            "[2:a]volume=-8dB[a2];" +  // Ajusta o volume da trilha sonora
//                            "[a1][a2]amix=inputs=2:duration=first[aout]", // Combina os áudios mantendo a duração do vídeo
//                    "-map", "0:v",
//                    "-map", "[aout]",
//                    "-c:v", "copy",
//                    "-c:a", "aac",
//                    "-strict", "experimental",
//                    outputPath
//            );
//            ProcessBuilder pb = new ProcessBuilder(
//                    "ffmpeg",
//                    "-i", videoPath,
//                    "-i", audioTreated,
//                    "-i", soundtrackOutput,
//                    "-filter_complex",
//                    "[1:a]volume=1.0[a1];" +
//                            "[2:a]volume=-8dB[a2];" +  // Ajusta o volume da trilha sonora
//                            "[a1][a2]amix=inputs=2:duration=first:dropout_transition=3[aout]", // Combina os áudios mantendo a duração do vídeo
//                    "-map", "0:v",
//                    "-map", "[aout]",
//                    "-c:v", "copy",
//                    "-c:a", "aac",
//                    "-strict", "experimental",
//                    outputPath
//            );

            //teste 22
            //"[0:v][logo_transparente]overlay=W-w-10:H-h-10[vout]",  // Posiciona o logo no canto inferior direito
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-i", videoPath,
                    "-i", audioTreated,
                    "-i", soundtrackOutput,
                    "-i", logoPath,  // -i arquivos de entrada
                    "-filter_complex",
                    "[1:a]volume=1.0[a1];" +
                            "[2:a]volume=-8dB[a2];" +  // Ajusta o volume da trilha sonora
                            "[a1][a2]amix=inputs=2:duration=first:dropout_transition=3[aout];" + // Combina os áudios mantendo a duração do vídeo
                            "[3:v]chromakey=0x00FF00:0.1:0.2,scale=iw*0.2:ih*0.2[logo_transparente];" +  // Remove fundo verde e redimensiona o logo em 80%
                            "[0:v][logo_transparente]overlay=10:H-h-10[vout]",  // Posiciona o logo no canto inferior esquerdo
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
}
