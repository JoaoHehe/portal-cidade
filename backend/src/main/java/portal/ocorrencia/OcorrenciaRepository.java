package portal.ocorrencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    // O Spring monta o SQL pelo nome: busca as ocorrências de um cidadão,
    // da mais nova para a mais antiga.
    // (cidadao.id → "CidadaoId", criadoEm → "CriadoEm")
    List<Ocorrencia> findByCidadaoIdOrderByCriadoEmDesc(Long cidadaoId);
}