package portal.ocorrencia.dto;

import java.time.LocalDateTime;

import portal.ocorrencia.TipoFoto;

public record FotoResponse(
        Long id,
        TipoFoto tipo,
        LocalDateTime enviadoEm,
        String url
) {
}