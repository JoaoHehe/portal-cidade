package portal.ocorrencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotoRepository extends JpaRepository<Foto, Long> {

    // Quantas fotos de determinado tipo uma ocorrência já tem
    long countByOcorrenciaIdAndTipo(Long ocorrenciaId, TipoFoto tipo);

    // Todas as fotos de uma ocorrência, da mais antiga para a mais nova
    List<Foto> findByOcorrenciaIdOrderByEnviadoEmAsc(Long ocorrenciaId);
}