package portal.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import portal.auth.dto.LoginRequest;
import portal.auth.dto.LoginResponse;
import portal.erro.RegraDeNegocioException;
import portal.usuario.Usuario;
import portal.usuario.UsuarioRepository;
import portal.usuario.dto.UsuarioResponse;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest dados) {

        // Procura o usuário pelo e-mail. Se não achar, o erro é IGUAL ao de
        // senha errada, de propósito: assim ninguém descobre quais e-mails existem.
        Usuario usuario = usuarioRepository.findByEmail(dados.email())
                .orElseThrow(this::credenciaisInvalidas);

        // matches() compara a senha digitada com o hash guardado no banco
        if (!passwordEncoder.matches(dados.senha(), usuario.getSenhaHash())) {
            throw credenciaisInvalidas();
        }

        String token = tokenService.gerarToken(usuario);

        UsuarioResponse resposta = new UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());

        return new LoginResponse(token, "Bearer", resposta);
    }

    private RegraDeNegocioException credenciaisInvalidas() {
        return new RegraDeNegocioException(
                "E-mail ou senha inválidos", HttpStatus.UNAUTHORIZED);
    }
}