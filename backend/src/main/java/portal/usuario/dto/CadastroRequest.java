package portal.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Dados que o cidadão envia para se cadastrar.
// "record" é uma classe enxuta só para carregar dados:
// o Java já cria construtor e getters sozinho (nome(), email(), senha()).
public record CadastroRequest(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String senha
) {
}