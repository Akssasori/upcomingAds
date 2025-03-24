package com.globo.upcomingAds.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record VideoRequest(
        @NotBlank(message = "O texto da locução é obrigatório")
        @Size(min = 10, max = 5000, message = "O texto deve ter entre 10 e 5000 caracteres")
        String texto,

        @NotBlank(message = "O UUID do locutor é obrigatório")
        String locutorUuid,

        String musicaCaminho,

        String videoPath,

        String logo,  // Dados da imagem em base64 ou URL

        String fileName

) implements Serializable {
    /**
     * Verifica se o logo está em formato base64
     * @return true se o logo estiver em formato base64
     */
    public boolean isLogoBase64() {
        return logo != null && logo.contains("base64");
    }

    /**
     * Retorna uma versão resumida do texto para logs
     * @return Texto truncado para exibição em logs
     */
    public String getShortText() {
        if (texto == null) return null;
        return texto.length() <= 50 ? texto : texto.substring(0, 47) + "...";
    }

    /**
     * Construtor com validação adicional
     */
    public VideoRequest {
        // Validações adicionais podem ser feitas aqui
        if (texto != null) {
            texto = texto.trim();
        }

        if (locutorUuid != null) {
            locutorUuid = locutorUuid.trim();
        }
    }

    /**
     * Retorna uma representação em string do objeto para logs,
     * ocultando dados sensíveis ou muito grandes
     */
    @Override
    public String toString() {
        return "VideoRequest{" +
                "texto='" + getShortText() + '\'' +
                ", locutorUuid='" + locutorUuid + '\'' +
                ", temMusica=" + (musicaCaminho != null && !musicaCaminho.isEmpty()) +
                ", temVideo=" + (videoPath != null && !videoPath.isEmpty()) +
                ", temLogo=" + (logo != null && !logo.isEmpty()) +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
