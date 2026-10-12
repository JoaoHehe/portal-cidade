package portal.ocorrencia.dto;

import java.util.List;

// A ocorrência completa: dados + fotos + histórico de status
public record OcorrenciaDetalheResponse(
        OcorrenciaResponse ocorrencia,
        List<FotoResponse> fotos,
        List<HistoricoResponse> historico
) {
}