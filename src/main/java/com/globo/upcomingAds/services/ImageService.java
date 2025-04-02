package com.globo.upcomingAds.services;

import com.globo.upcomingAds.client.ChatGptClient;
import com.globo.upcomingAds.dtos.request.PromptGptDTO;
import com.globo.upcomingAds.dtos.request.chatGpt.ChatGptImageRequest;
import com.globo.upcomingAds.dtos.response.chatGpt.ChatGptImageResponse;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ImageService {

    private final ChatGptClient chatGptClient;

    public ImageService(ChatGptClient chatGptClient) {
        this.chatGptClient = chatGptClient;
    }


    public byte[] generateImage(PromptGptDTO prompt) {


        ChatGptImageRequest request = ChatGptImageRequest.builder()
                .prompt(prompt.getPrompt())
                .model("dall-e-3")
                .responseFormat("b64_json")
                .n(1)
                .size("1024x1024")
                .build();

        ChatGptImageResponse response = chatGptClient.generateImage(request);
        String base64Image = response.getData().getFirst().getB64Json();
        return Base64.getDecoder().decode(base64Image);

    }
}
