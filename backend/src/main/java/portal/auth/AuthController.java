package portal.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import portal.usuario.UsuarioService;
import portal.usuario.dto.CadastroRequest;
import portal.usuario.dto.UsuarioResponse;

@RestController
@RequestMapping("/api/auth")   // todos os endpoints daqui começam com /api/auth
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/auth/cadastro
    // @Valid liga as validações do CadastroRequest (@NotBlank, @Email...)
    // @RequestBody transforma o JSON recebido no objeto CadastroRequest
    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)   // responde 201 (criado) em vez de 200
    public UsuarioResponse cadastrar(@Valid @RequestBody CadastroRequest dados) {
        return usuarioService.cadastrar(dados);
    }
}