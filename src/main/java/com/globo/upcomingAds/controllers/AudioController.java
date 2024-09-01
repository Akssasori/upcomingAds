package com.globo.upcomingAds.controllers;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("audio")
public class AudioController {

    @Value("${api.elevenlabs.key}")
    private String API_KEY;


    @PostMapping("/convert-text-to-speech")
    public ResponseEntity<String> convertTextToSpeech(@RequestParam String voiceId, @RequestBody String text) {

    try {

        HttpResponse<String> response = Unirest.post("https://api.elevenlabs.io/v1/text-to-speech/" + voiceId)
                .header("xi-api-key", API_KEY)
                .header("Content-Type", "application/json")
                .body("{\"text\":\"" + text + "\"}")
                .asString();

        if (response.getStatus() != 200) {
            return ResponseEntity.status(response.getStatus()).body("Erro na requisição para a API: " + response.getStatusText());
        }

        InputStream inputStream = convertResponseInputStream(response);

        // Caminho onde o arquivo será salvo
        Path outputPath = Paths.get("C:/hack/automatizacao/audio_output.mpga");
        File outputFile = outputPath.toFile();

        // Salvando o arquivo no sistema de arquivos
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        return ResponseEntity.ok("Áudio salvo com sucesso em: " + outputPath.toString());

    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao salvar o arquivo de áudio: " + e.getMessage());
    }

    }

    private InputStream convertResponseInputStream(HttpResponse<String> response) {
        byte[] audioBytes = response.getBody().getBytes();
        InputStream inputStream = new ByteArrayInputStream(audioBytes);
        return inputStream;
    }

}
