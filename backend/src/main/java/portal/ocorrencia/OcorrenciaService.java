package portal.ocorrencia;

import java.util.Comparator;
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

    // Duas ocorrências da mesma categoria a menos de 100 m são "possíveis duplicatas"
    private static final double RAIO_DUPLICATA_METROS = 100;

    private final OcorrenciaRepository ocorrenciaRepository;
    private final HistoricoStatusRepository historicoRepository;
    private final FotoRepository fotoRepository;
    private final ConfirmacaoRepository confirmacaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GeradorDeProtocolo geradorDeProtocolo;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository,
                             HistoricoStatusRepository historicoRepository,
                             FotoRepository fotoRepository,
                             ConfirmacaoRepository confirmacaoRepository,
                             CategoriaRepository categoriaRepository,
                             UsuarioRepository usuarioRepository,
                             GeradorDeProtocolo geradorDeProtocolo) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.historicoRepository = historicoRepository;
        this.fotoRepository = fotoRepository;
        this.confirmacaoRepository = confirmacaoRepository;
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
        // localCritico fica false por enquanto (no Passo 23 o sistema decide)

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

    // Procura ocorrências abertas, da mesma categoria, num raio de 100 m
    @Transactional(readOnly = true)
    public List<OcorrenciaProximaResponse> buscarProximas(double latitude, double longitude,
                                                          Long categoriaId) {

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new RegraDeNegocioException("Coordenadas inválidas");
        }

        // Converte 100 m em "graus" para montar a caixinha.
        // 1 grau de latitude ≈ 111.000 m. Já a longitude "encolhe" conforme
        // nos afastamos da linha do Equador, por isso o cosseno.
        double variacaoLat = RAIO_DUPLICATA_METROS / 111_000.0;
        double variacaoLng = RAIO_DUPLICATA_METROS
                / (111_000.0 * Math.cos(Math.toRadians(latitude)));

        // Etapa 1: o banco devolve só quem está na caixinha (RESOLVIDO fica de fora)
        List<Ocorrencia> candidatas = ocorrenciaRepository.buscarNaCaixa(
                categoriaId, StatusOcorrencia.RESOLVIDO,
                latitude - variacaoLat, latitude + variacaoLat,
                longitude - variacaoLng, longitude + variacaoLng);

        // Etapa 2: calcula a distância exata, descarta quem passou de 100 m
        // (a caixinha tem "quinas" mais longe que o raio) e ordena do mais perto ao mais longe
        return candidatas.stream()
                .map(o -> {
                    double distancia = CalculadoraDeDistancia.emMetros(
                            latitude, longitude,
                            o.getLocalizacao().getLatitude(),
                            o.getLocalizacao().getLongitude());
                    return new OcorrenciaProximaResponse(
                            o.getId(), o.getProtocolo(), o.getTitulo(), o.getStatus(),
                            o.getLocalizacao().getLatitude(),
                            o.getLocalizacao().getLongitude(),
                            Math.round(distancia * 10) / 10.0,   // 1 casa decimal
                            confirmacaoRepository.countByOcorrenciaId(o.getId()));
                })
                .filter(r -> r.distanciaMetros() <= RAIO_DUPLICATA_METROS)
                .sorted(Comparator.comparingDouble(OcorrenciaProximaResponse::distanciaMetros))
                .toList();
    }

    // O cidadão diz: "esse problema também me afeta"
    @Transactional
    public ConfirmacaoResponse confirmar(Long ocorrenciaId, Long usuarioId) {

        Ocorrencia ocorrencia = ocorrenciaRepository.findById(ocorrenciaId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Ocorrência não encontrada", HttpStatus.NOT_FOUND));

        // Regra 1: não adianta confirmar o que já foi resolvido
        if (ocorrencia.getStatus() == StatusOcorrencia.RESOLVIDO) {
            throw new RegraDeNegocioException(
                    "Esta ocorrência já foi resolvida e não aceita mais confirmações");
        }

        // Regra 2: quem registrou já "votou" ao registrar
        if (ocorrencia.getCidadao().getId().equals(usuarioId)) {
            throw new RegraDeNegocioException(
                    "Você registrou esta ocorrência, então ela já conta com a sua voz");
        }

        // Regra 3: uma confirmação por pessoa (409 = "isso já existe")
        if (confirmacaoRepository.existsByOcorrenciaIdAndUsuarioId(ocorrenciaId, usuarioId)) {
            throw new RegraDeNegocioException(
                    "Você já confirmou esta ocorrência", HttpStatus.CONFLICT);
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Usuário não encontrado", HttpStatus.NOT_FOUND));

        Confirmacao confirmacao = new Confirmacao();
        confirmacao.setOcorrencia(ocorrencia);
        confirmacao.setUsuario(usuario);
        confirmacaoRepository.save(confirmacao);

        return new ConfirmacaoResponse(
                ocorrenciaId, confirmacaoRepository.countByOcorrenciaId(ocorrenciaId));
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
                o.getCriadoEm(),
                confirmacaoRepository.countByOcorrenciaId(o.getId()));
    }
}