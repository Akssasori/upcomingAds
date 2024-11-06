package com.globo.upcomingAds.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ConversionService {


    public String convert(String folderPath) {

        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            return "Pasta não encontrada ou não é um diretório válido!";
        }

        File[] mp4Files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
        if (mp4Files == null || mp4Files.length == 0) {
            return "Nenhum arquivo .mp4 encontrado na pasta!";
        }
        for (File mp4File : mp4Files) {
            String inputPath = mp4File.getAbsolutePath();
            String outputPath = inputPath.replace(".mp4", ".mxf");

            try {
                // Comando FFmpeg para conversão
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "ffmpeg", "-i", inputPath, "-c:v", "mpeg2video", "-b:v", "50M", "-c:a", "pcm_s16le", outputPath
                );
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return "Erro ao converter o arquivo: " + mp4File.getName();
            }
        }

        return "Conversão concluída para todos os arquivos .mp4!";


    }
}
