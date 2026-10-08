package portal;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Endpoint só para testar se a API está no ar.
// Vamos apagar isso quando tivermos endpoints de verdade.
@RestController
public class PingController {

    // Quando alguém acessar /api/ping com GET, este método responde
    @GetMapping("/api/ping")
    public Map<String, String> ping() {
        // O Spring transforma este Map em JSON automaticamente
        return Map.of("mensagem", "Portal do Cidadão no ar!");
    }
}