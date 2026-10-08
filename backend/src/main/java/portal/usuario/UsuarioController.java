package portal.usuario;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // GET /api/usuarios/me  ->  "quem sou eu?"
    // O Spring já conferiu o token antes de chegar aqui; se chegou,
    // o token é válido. @AuthenticationPrincipal entrega os dados dele.
    @GetMapping("/me")
    public Map<String, Object> eu(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "email", jwt.getSubject(),
                "id", jwt.getClaim("id"),
                "perfil", jwt.getClaim("perfil"));
    }
}