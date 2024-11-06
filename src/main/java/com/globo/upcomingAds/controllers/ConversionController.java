package com.globo.upcomingAds.controllers;

import com.globo.upcomingAds.services.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("conversion")
public class ConversionController {

    public static final String FOLDER_PATH = "C:\\hack\\automatizacao\\trilhaSonoraOutput.mp3";

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


    @GetMapping("/mp4-to-mxf")
    public String convertMp4ToMxf() {
        return conversionService.convert(FOLDER_PATH);
    }
}
