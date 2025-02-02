package com.globo.upcomingAds.client;

import com.globo.upcomingAds.configs.ClientElevenLabsConfig;
import com.globo.upcomingAds.dtos.request.AudioRequestDTO;
import com.globo.upcomingAds.dtos.response.VoiceIdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "${ai.elevenLabs.name}",
        url = "${ai.elevenLabs.url}",configuration = ClientElevenLabsConfig.class)
public interface ElevenLabsClient {

    @PostMapping(value = "/v1/text-to-speech/{voiceId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] convertTextToSpeech(@PathVariable("voiceId") String voiceId,
                               @RequestBody AudioRequestDTO requestBody);

    @PostMapping(value = "/v1/text-to-speech/{voiceId}/stream",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    byte[] convertTextToSpeechStream(@PathVariable("voiceId") String voiceId,
                               @RequestBody AudioRequestDTO requestBody);

    @PostMapping(value = "/v1/voices/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<VoiceIdDTO> addVoice(
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("name") String name,
            @RequestPart("remove_background_noise") Boolean removeBackgroundNoise,
            @RequestPart("description") String description);
}
