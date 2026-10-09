package portal.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import portal.usuario.Perfil;
import portal.usuario.Usuario;
import portal.usuario.UsuarioRepository;

// CommandLineRunner = "rode o método run() uma vez, logo depois que o app subir".
// Serve para criar os usuários de teste (o projeto é fictício!).
// Num sistema real, NUNCA deixaríamos senhas fixas no código.
@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DadosIniciais(UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder,
                         JdbcTemplate jdbcTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        // nome, e-mail, senha, perfil, sigla do departamento (null = nenhum)
        criarSeNaoExistir("Administrador", "admin@prefeitura.com", "admin123", Perfil.ADMIN, null);
        criarSeNaoExistir("Carlos Obras", "carlos@prefeitura.com", "func123", Perfil.FUNCIONARIO, "SEINFRA");
        criarSeNaoExistir("Ana Iluminação", "ana@prefeitura.com", "func123", Perfil.FUNCIONARIO, "ILUMIN");
    }

    private void criarSeNaoExistir(String nome, String email, String senha,
                                   Perfil perfil, String siglaDepartamento) {

        // Se já existe, não faz nada. Assim, rodar o app várias vezes
        // não cria usuários repetidos.
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setPerfil(perfil);

        // Funcionários pertencem a um departamento. Buscamos o id pela sigla
        // (SEINFRA, ILUMIN...) que cadastramos na migration V2.
        if (siglaDepartamento != null) {
            Long idDepartamento = jdbcTemplate.queryForObject(
                    "SELECT id FROM departamento WHERE sigla = ?",
                    Long.class,
                    siglaDepartamento);
            usuario.setDepartamentoId(idDepartamento);
        }

        usuarioRepository.save(usuario);
    }
}