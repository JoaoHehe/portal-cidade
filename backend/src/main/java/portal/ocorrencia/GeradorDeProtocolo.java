package portal.ocorrencia;

import java.time.Year;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// Gera números de protocolo como "2026-000001"
@Component
public class GeradorDeProtocolo {

    private final JdbcTemplate jdbcTemplate;

    public GeradorDeProtocolo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String proximoProtocolo() {
        // nextval pede ao banco o próximo número do contador (1, 2, 3...).
        // O banco garante que nunca repete, mesmo com várias pessoas ao mesmo tempo.
        Long numero = jdbcTemplate.queryForObject("SELECT nextval('protocolo_seq')", Long.class);

        // %d = o ano, %06d = o número com 6 dígitos, completando com zeros
        return String.format("%d-%06d", Year.now().getValue(), numero);
    }
}