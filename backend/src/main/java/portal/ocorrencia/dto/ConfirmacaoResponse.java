package portal.ocorrencia.dto;

// Resposta de "confirmar": quantas pessoas já confirmaram no total
public record ConfirmacaoResponse(
        Long ocorrenciaId,
        long confirmacoes
) {
}