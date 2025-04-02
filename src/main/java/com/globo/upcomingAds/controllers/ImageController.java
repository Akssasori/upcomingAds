package com.globo.upcomingAds.controllers;

import com.globo.upcomingAds.dtos.request.PromptGptDTO;
import com.globo.upcomingAds.dtos.request.chatGpt.ChatGptImageRequest;
import com.globo.upcomingAds.services.ImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("imagem")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/gerar")
    public ResponseEntity<byte[]> gerarImagem(@RequestBody PromptGptDTO prompt) {

        byte[] imageBytes  = imageService.generateImage(prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(imageBytes , headers, HttpStatus.OK);

    }



}
