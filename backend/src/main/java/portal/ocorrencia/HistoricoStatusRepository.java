package portal.ocorrencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {

    // O "diário" da ocorrência, na ordem em que as coisas aconteceram
    List<HistoricoStatus> findByOcorrenciaIdOrderByCriadoEmAsc(Long ocorrenciaId);
}