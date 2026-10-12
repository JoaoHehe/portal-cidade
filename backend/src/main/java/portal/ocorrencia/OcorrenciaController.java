package portal.ocorrencia;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import portal.ocorrencia.dto.CriarOcorrenciaRequest;
import portal.ocorrencia.dto.FotoResponse;
import portal.ocorrencia.dto.OcorrenciaDetalheResponse;
import portal.ocorrencia.dto.OcorrenciaResponse;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;
    private final FotoService fotoService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService,
                                FotoService fotoService) {
        this.ocorrenciaService = ocorrenciaService;
        this.fotoService = fotoService;
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

    // GET /api/ocorrencias/{id}  ->  abrir uma ocorrência (com fotos e histórico)
    @GetMapping("/{id}")
    public OcorrenciaDetalheResponse detalhar(@AuthenticationPrincipal Jwt jwt,
                                              @PathVariable Long id) {
        return ocorrenciaService.detalhar(id, idDoUsuario(jwt));
    }

    // POST /api/ocorrencias/{id}/fotos  ->  enviar uma foto "antes"
    // O arquivo vai no campo de formulário chamado "arquivo"
    @PostMapping("/{id}/fotos")
    @ResponseStatus(HttpStatus.CREATED)
    public FotoResponse enviarFoto(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable Long id,
                                   @RequestParam("arquivo") MultipartFile arquivo) {
        return fotoService.enviarFotoAntes(id, idDoUsuario(jwt), arquivo);
    }

    // O id vem de DENTRO do token, nunca do JSON enviado pelo usuário.
    // Se viesse do JSON, qualquer pessoa poderia registrar ocorrências
    // "em nome" de outra, bastando trocar o número.
    private Long idDoUsuario(Jwt jwt) {
        Number id = jwt.getClaim("id");
        return id.longValue();
    }
}