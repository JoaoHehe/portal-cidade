package portal.ocorrencia.dto;

import java.time.LocalDateTime;

import portal.ocorrencia.Prioridade;
import portal.ocorrencia.StatusOcorrencia;

// Dados da ocorrência que a API devolve
public record OcorrenciaResponse(
        Long id,
        String protocolo,
        String titulo,
        String descricao,
        String categoria,
        StatusOcorrencia status,
        Prioridade prioridade,
        double latitude,
        double longitude,
        String endereco,
        String bairro,
        LocalDateTime criadoEm
) {
}