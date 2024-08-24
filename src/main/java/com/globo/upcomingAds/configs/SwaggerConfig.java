package com.globo.upcomingAds.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    public OpenAPI microserviceOpenAPI(){
        return new OpenAPI().info(
                new Info().title("upcomingAds")
                        .description("Plataforma para criação de templates de vídeos com base na emoção do evento patrocinado")
                        .version("1.0")
                        .contact(new Contact().email("lucas.diniz@g.globo")));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }
}
