package portal.erro;

import java.util.Map;

// O JSON que a API devolve quando algo dá errado.
// "campos" só é preenchido nos erros de validação (qual campo errou e por quê).
public record ErroResponse(
        int status,
        String mensagem,
        Map<String, String> campos
) {
}