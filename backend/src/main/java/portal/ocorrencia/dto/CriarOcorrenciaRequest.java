package portal.ocorrencia.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Dados que o cidadão envia ao registrar uma ocorrência.
// Repare no que NÃO está aqui: status, prioridade, protocolo e quem é o cidadão.
// Isso o sistema define sozinho (o cidadão não pode "escolher" ser URGENTE).
public record CriarOcorrenciaRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String descricao,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId,

        @NotNull(message = "A latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "Latitude inválida")
        @DecimalMax(value = "90.0", message = "Latitude inválida")
        Double latitude,

        @NotNull(message = "A longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "Longitude inválida")
        @DecimalMax(value = "180.0", message = "Longitude inválida")
        Double longitude,

        String endereco,

        @NotBlank(message = "O bairro é obrigatório")
        String bairro
) {
}