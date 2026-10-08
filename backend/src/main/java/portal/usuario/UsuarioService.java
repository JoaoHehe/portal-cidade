package portal.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import portal.erro.RegraDeNegocioException;
import portal.usuario.dto.CadastroRequest;
import portal.usuario.dto.UsuarioResponse;

// @Service = classe com as regras do sistema (o "cérebro")
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // O Spring entrega esses dois objetos prontos pelo construtor
    // (isso se chama "injeção de dependência")
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse cadastrar(CadastroRequest dados) {

        // Regra 1: não pode haver dois usuários com o mesmo e-mail
        if (usuarioRepository.existsByEmail(dados.email())) {
            throw new RegraDeNegocioException(
                    "Já existe um usuário com este e-mail", HttpStatus.CONFLICT);
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());

        // Regra 2: a senha NUNCA vai pura para o banco, só o hash dela
        usuario.setSenhaHash(passwordEncoder.encode(dados.senha()));

        // Regra 3: quem se cadastra pelo site é SEMPRE cidadão.
        // Se deixássemos o usuário escolher o perfil, qualquer um
        // se cadastraria como ADMIN. Funcionários serão criados por outro caminho.
        usuario.setPerfil(Perfil.CIDADAO);

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                salvo.getId(), salvo.getNome(), salvo.getEmail(), salvo.getPerfil());
    }
}