package portal.ocorrencia;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import portal.ocorrencia.dto.CriarOcorrenciaRequest;
import portal.ocorrencia.dto.OcorrenciaResponse;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    // POST /api/ocorrencias  ->  registrar uma nova ocorrência
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OcorrenciaResponse criar(@AuthenticationPrincipal Jwt jwt,
                                    @Valid @RequestBody CriarOcorrenciaRequest dados) {
        return ocorrenciaService.criar(idDoUsuario(jwt), dados);
    }

    // GET /api/ocorrencias/minhas  ->  as ocorrências de quem está logado
    @GetMapping("/minhas")
    public List<OcorrenciaResponse> minhas(@AuthenticationPrincipal Jwt jwt) {
        return ocorrenciaService.listarDoCidadao(idDoUsuario(jwt));
    }

    // O id vem de DENTRO do token, nunca do JSON enviado pelo usuário.
    // Se viesse do JSON, qualquer pessoa poderia registrar ocorrências
    // "em nome" de outra, bastando trocar o número.
    private Long idDoUsuario(Jwt jwt) {
        Number id = jwt.getClaim("id");
        return id.longValue();
    }
}