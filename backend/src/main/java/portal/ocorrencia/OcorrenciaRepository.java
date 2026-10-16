package portal.ocorrencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    // O Spring monta o SQL pelo nome: busca as ocorrências de um cidadão,
    // da mais nova para a mais antiga.
    // (cidadao.id → "CidadaoId", criadoEm → "CriadoEm")
    List<Ocorrencia> findByCidadaoIdOrderByCriadoEmDesc(Long cidadaoId);

    // Busca as ocorrências ABERTAS de uma categoria que estão dentro de uma
    // "caixinha" de coordenadas. É só o filtro rápido; a distância exata
    // é calculada depois, no Java.
    @Query("""
            SELECT o FROM Ocorrencia o
            WHERE o.categoria.id = :categoriaId
              AND o.status <> :statusIgnorado
              AND o.localizacao.latitude  BETWEEN :latMin AND :latMax
              AND o.localizacao.longitude BETWEEN :lngMin AND :lngMax
            """)
    List<Ocorrencia> buscarNaCaixa(@Param("categoriaId") Long categoriaId,
                                   @Param("statusIgnorado") StatusOcorrencia statusIgnorado,
                                   @Param("latMin") double latMin,
                                   @Param("latMax") double latMax,
                                   @Param("lngMin") double lngMin,
                                   @Param("lngMax") double lngMax);
}