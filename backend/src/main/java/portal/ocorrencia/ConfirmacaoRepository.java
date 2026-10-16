package portal.ocorrencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmacaoRepository extends JpaRepository<Confirmacao, Long> {

    // Quantas pessoas confirmaram esta ocorrência
    long countByOcorrenciaId(Long ocorrenciaId);

    // Esta pessoa já confirmou esta ocorrência?
    boolean existsByOcorrenciaIdAndUsuarioId(Long ocorrenciaId, Long usuarioId);
}