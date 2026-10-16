package portal.ocorrencia.dto;

import portal.ocorrencia.StatusOcorrencia;

// Uma ocorrência próxima, mostrada ao cidadão na hora de registrar a dele.
// Repare: não tem o nome de quem registrou (privacidade!).
public record OcorrenciaProximaResponse(
        Long id,
        String protocolo,
        String titulo,
        StatusOcorrencia status,
        double latitude,
        double longitude,
        double distanciaMetros,
        long confirmacoes
) {
}