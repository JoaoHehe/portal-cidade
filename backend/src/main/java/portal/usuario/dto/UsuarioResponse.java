package portal.usuario.dto;

import portal.usuario.Perfil;

// Dados do usuário que a API devolve. Repare: não tem senha aqui!
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil
) {
}