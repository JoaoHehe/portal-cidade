package portal.ocorrencia;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import portal.categoria.Categoria;
import portal.categoria.CategoriaRepository;
import portal.erro.RegraDeNegocioException;
import portal.ocorrencia.dto.*;
import portal.usuario.Usuario;
import portal.usuario.UsuarioRepository;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final HistoricoStatusRepository historicoRepository;
    private final FotoRepository fotoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeradorDeProtocolo geradorDeProtocolo;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository,
                             HistoricoStatusRepository historicoRepository,
                             FotoRepository fotoRepository,
                             CategoriaRepository categoriaRepository,
                             UsuarioRepository usuarioRepository,
                             GeradorDeProtocolo geradorDeProtocolo) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.historicoRepository = historicoRepository;
        this.fotoRepository = fotoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.geradorDeProtocolo = geradorDeProtocolo;
    }

    // @Transactional = "tudo ou nada": se algo falhar no meio (ex: ao salvar o
    // histórico), o banco desfaz também a ocorrência. Nada fica pela metade.
    @Transactional
    public OcorrenciaResponse criar(Long cidadaoId, CriarOcorrenciaRequest dados) {

        Categoria categoria = categoriaRepository.findById(dados.categoriaId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Categoria não encontrada", HttpStatus.NOT_FOUND));

        Usuario cidadao = usuarioRepository.findById(cidadaoId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Usuário não encontrado", HttpStatus.NOT_FOUND));

        Localizacao localizacao = new Localizacao();
        localizacao.setLatitude(dados.latitude());
        localizacao.setLongitude(dados.longitude());
        localizacao.setEndereco(dados.endereco());
        localizacao.setBairro(dados.bairro());
        // localCritico fica false por enquanto (na Etapa 4 o sistema decide)

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setProtocolo(geradorDeProtocolo.proximoProtocolo());
        ocorrencia.setTitulo(dados.titulo());
        ocorrencia.setDescricao(dados.descricao());
        ocorrencia.setCategoria(categoria);
        ocorrencia.setCidadao(cidadao);
        ocorrencia.setLocalizacao(localizacao);

        // A gravidade inicial vem da categoria (ex: Saneamento = 5).
        // status = RECEBIDO e prioridade = MEDIA já são os valores padrão da classe.
        ocorrencia.setGravidade(categoria.getGravidadeBase());

        Ocorrencia salva = ocorrenciaRepository.save(ocorrencia);

        // Primeira linha do diário: "nasceu como RECEBIDO"
        HistoricoStatus historico = new HistoricoStatus();
        historico.setOcorrencia(salva);
        historico.setStatusAnterior(null);
        historico.setStatusNovo(StatusOcorrencia.RECEBIDO);
        historico.setUsuario(cidadao);
        historico.setObservacao("Ocorrência registrada pelo cidadão");
        historicoRepository.save(historico);

        return paraResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaResponse> listarDoCidadao(Long cidadaoId) {
        return ocorrenciaRepository.findByCidadaoIdOrderByCriadoEmDesc(cidadaoId)
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    // Abre uma ocorrência com fotos e histórico
    @Transactional(readOnly = true)
    public OcorrenciaDetalheResponse detalhar(Long ocorrenciaId, Long cidadaoId) {

        Ocorrencia ocorrencia = ocorrenciaRepository.findById(ocorrenciaId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Ocorrência não encontrada", HttpStatus.NOT_FOUND));

        // Por enquanto o cidadão só enxerga as próprias ocorrências
        if (!ocorrencia.getCidadao().getId().equals(cidadaoId)) {
            throw new RegraDeNegocioException(
                    "Você só pode ver as suas próprias ocorrências", HttpStatus.FORBIDDEN);
        }

        // Converte cada Foto (entidade) em FotoResponse (o que vai pro JSON)
        List<FotoResponse> fotos = fotoRepository
                .findByOcorrenciaIdOrderByEnviadoEmAsc(ocorrenciaId)
                .stream()
                .map(f -> new FotoResponse(f.getId(), f.getTipo(), f.getEnviadoEm(),
                        "/api/fotos/" + f.getId() + "/arquivo"))
                .toList();

        // Faz o mesmo com cada linha do histórico
        List<HistoricoResponse> historico = historicoRepository
                .findByOcorrenciaIdOrderByCriadoEmAsc(ocorrenciaId)
                .stream()
                .map(h -> new HistoricoResponse(h.getStatusAnterior(), h.getStatusNovo(),
                        h.getObservacao(), h.getUsuario().getNome(), h.getCriadoEm()))
                .toList();

        return new OcorrenciaDetalheResponse(paraResponse(ocorrencia), fotos, historico);
    }

    // Converte a entidade (banco) no DTO (o que vai pro JSON)
    private OcorrenciaResponse paraResponse(Ocorrencia o) {
        return new OcorrenciaResponse(
                o.getId(),
                o.getProtocolo(),
                o.getTitulo(),
                o.getDescricao(),
                o.getCategoria().getNome(),
                o.getStatus(),
                o.getPrioridade(),
                o.getLocalizacao().getLatitude(),
                o.getLocalizacao().getLongitude(),
                o.getLocalizacao().getEndereco(),
                o.getLocalizacao().getBairro(),
                o.getCriadoEm());
    }
}