package portal.ocorrencia;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    private final FotoService fotoService;

    public FotoController(FotoService fotoService) {
        this.fotoService = fotoService;
    }

    // GET /api/fotos/{id}/arquivo  ->  a imagem em si
    @GetMapping("/{id}/arquivo")
    public ResponseEntity<Resource> arquivo(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long id) {

        Number usuarioId = jwt.getClaim("id");
        String perfil = jwt.getClaim("perfil");
        boolean ehFuncionario = !"CIDADAO".equals(perfil);

        Resource imagem = fotoService.buscarArquivo(id, usuarioId.longValue(), ehFuncionario);

        // Avisa ao navegador qual é o tipo da imagem, pela extensão do arquivo
        String nome = imagem.getFilename();
        MediaType tipo = MediaType.IMAGE_JPEG;
        if (nome != null && nome.endsWith(".png")) {
            tipo = MediaType.IMAGE_PNG;
        } else if (nome != null && nome.endsWith(".webp")) {
            tipo = MediaType.parseMediaType("image/webp");
        }

        return ResponseEntity.ok().contentType(tipo).body(imagem);
    }
}