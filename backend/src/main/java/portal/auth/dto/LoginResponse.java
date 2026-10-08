package portal.auth.dto;

import portal.usuario.dto.UsuarioResponse;

// O que devolvemos após um login bem-sucedido
public record LoginResponse(
        String token,
        String tipo,            // sempre "Bearer" (é o padrão para tokens JWT)
        UsuarioResponse usuario
) {
}