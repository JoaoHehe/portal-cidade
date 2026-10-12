package portal.ocorrencia.dto;

import java.time.LocalDateTime;

import portal.ocorrencia.StatusOcorrencia;

// Uma linha do "diário" de status
public record HistoricoResponse(
        StatusOcorrencia statusAnterior,   // null no primeiro registro
        StatusOcorrencia statusNovo,
        String observacao,
        String usuario,                    // nome de quem fez a mudança
        LocalDateTime criadoEm
) {
}