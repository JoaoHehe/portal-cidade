package portal.ocorrencia;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;


import portal.erro.RegraDeNegocioException;
import portal.ocorrencia.dto.FotoResponse;

@Service
public class FotoService {

    // Máximo de fotos "antes" por ocorrência
    private static final long MAXIMO_DE_FOTOS = 5;

    // Tipos de imagem aceitos e a extensão que usamos para cada um
    private static final Map<String, String> TIPOS_ACEITOS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final OcorrenciaRepository ocorrenciaRepository;
    private final FotoRepository fotoRepository;
    private final ArmazenamentoDeArquivos armazenamento;

    public FotoService(OcorrenciaRepository ocorrenciaRepository,
                       FotoRepository fotoRepository,
                       ArmazenamentoDeArquivos armazenamento) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.fotoRepository = fotoRepository;
        this.armazenamento = armazenamento;
    }

    @Transactional
    public FotoResponse enviarFotoAntes(Long ocorrenciaId, Long cidadaoId, MultipartFile arquivo) {

        Ocorrencia ocorrencia = ocorrenciaRepository.findById(ocorrenciaId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Ocorrência não encontrada", HttpStatus.NOT_FOUND));

        // Regra 1: só o dono da ocorrência pode adicionar fotos a ela
        if (!ocorrencia.getCidadao().getId().equals(cidadaoId)) {
            throw new RegraDeNegocioException(
                    "Você só pode adicionar fotos às suas próprias ocorrências",
                    HttpStatus.FORBIDDEN);
        }

        // Regra 2: o arquivo não pode estar vazio
        if (arquivo.isEmpty()) {
            throw new RegraDeNegocioException("Nenhum arquivo foi enviado");
        }

        // Regra 3: só imagens JPG, PNG ou WEBP
        String tipoDoArquivo = arquivo.getContentType();
        String extensao = (tipoDoArquivo == null) ? null : TIPOS_ACEITOS.get(tipoDoArquivo);
        if (extensao == null) {
            throw new RegraDeNegocioException("Envie uma imagem JPG, PNG ou WEBP");
        }

        // Regra 4: limite de fotos por ocorrência
        long jaEnviadas = fotoRepository.countByOcorrenciaIdAndTipo(ocorrenciaId, TipoFoto.ANTES);
        if (jaEnviadas >= MAXIMO_DE_FOTOS) {
            throw new RegraDeNegocioException(
                    "Limite de " + MAXIMO_DE_FOTOS + " fotos por ocorrência atingido");
        }

        String nomeDoArquivo = armazenamento.salvar(arquivo, extensao);

        Foto foto = new Foto();
        foto.setOcorrencia(ocorrencia);
        foto.setCaminho(nomeDoArquivo);
        foto.setTipo(TipoFoto.ANTES);
        Foto salva = fotoRepository.save(foto);

        return new FotoResponse(salva.getId(), salva.getTipo(), salva.getEnviadoEm(),
                "/api/fotos/" + salva.getId() + "/arquivo");
    }

    // Devolve o arquivo da foto, se a pessoa tiver permissão para vê-lo.
    // Dono da ocorrência: pode. Funcionário/admin: pode. Outro cidadão: não.
    @Transactional(readOnly = true)
    public Resource buscarArquivo(Long fotoId, Long usuarioId, boolean ehFuncionario) {

        Foto foto = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Foto não encontrada", HttpStatus.NOT_FOUND));

        boolean ehDono = foto.getOcorrencia().getCidadao().getId().equals(usuarioId);

        if (!ehDono && !ehFuncionario) {
            throw new RegraDeNegocioException(
                    "Você não tem permissão para ver esta foto", HttpStatus.FORBIDDEN);
        }

        return armazenamento.carregar(foto.getCaminho());
    }

}