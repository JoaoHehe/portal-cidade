package portal.ocorrencia;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import portal.categoria.Categoria;
import portal.categoria.CategoriaRepository;
import portal.erro.RegraDeNegocioException;
import portal.ocorrencia.dto.CriarOcorrenciaRequest;
import portal.ocorrencia.dto.OcorrenciaResponse;
import portal.usuario.Usuario;
import portal.usuario.UsuarioRepository;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final HistoricoStatusRepository historicoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeradorDeProtocolo geradorDeProtocolo;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository,
                             HistoricoStatusRepository historicoRepository,
                             CategoriaRepository categoriaRepository,
                             UsuarioRepository usuarioRepository,
                             GeradorDeProtocolo geradorDeProtocolo) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.historicoRepository = historicoRepository;
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