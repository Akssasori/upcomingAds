package com.globo.upcomingAds.client;

import com.globo.upcomingAds.configs.FeignClientConfig;
import com.globo.upcomingAds.dtos.request.AudioRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${ai.elevenLabs.name}",
        url = "${ai.elevenLabs.url}",configuration = FeignClientConfig.class)
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
}
