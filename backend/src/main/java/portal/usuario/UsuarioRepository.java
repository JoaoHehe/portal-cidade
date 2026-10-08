package portal.usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// Só de estender JpaRepository, você ganha de graça: save, findById,
// findAll, delete... sem escrever nenhum SQL!
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring entende o NOME do método e monta o SQL sozinho:
    // SELECT * FROM usuario WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // SELECT COUNT(*) > 0 FROM usuario WHERE email = ?
    boolean existsByEmail(String email);
}