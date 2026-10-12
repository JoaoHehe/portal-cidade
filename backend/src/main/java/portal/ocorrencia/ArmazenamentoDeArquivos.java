package portal.ocorrencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import portal.erro.RegraDeNegocioException;

// Cuida de gravar os arquivos na pasta de uploads
@Component
public class ArmazenamentoDeArquivos {

    private final Path pasta;

    // @Value pega o valor de app.uploads.pasta lá do application.yml
    public ArmazenamentoDeArquivos(@Value("${app.uploads.pasta}") String nomeDaPasta) {
        this.pasta = Path.of(nomeDaPasta).toAbsolutePath().normalize();
        try {
            // Cria a pasta se ela ainda não existir
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar a pasta de uploads", e);
        }
    }

    // Grava o arquivo com um nome inventado (UUID) e devolve esse nome
    public String salvar(MultipartFile arquivo, String extensao) {
        String nomeFinal = UUID.randomUUID() + "." + extensao;
        try {
            Files.copy(arquivo.getInputStream(), pasta.resolve(nomeFinal));
        } catch (IOException e) {
            throw new RegraDeNegocioException(
                    "Não foi possível salvar o arquivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return nomeFinal;
    }

    // Localiza um arquivo já salvo na pasta de uploads
    public Resource carregar(String nomeDoArquivo) {
        Path arquivo = pasta.resolve(nomeDoArquivo).normalize();

        // Proteção extra: o arquivo TEM que estar dentro da pasta de uploads
        if (!arquivo.startsWith(pasta)) {
            throw new RegraDeNegocioException("Arquivo inválido");
        }

        Resource recurso = new FileSystemResource(arquivo);
        if (!recurso.exists()) {
            throw new RegraDeNegocioException(
                    "O arquivo da foto não foi encontrado", HttpStatus.NOT_FOUND);
        }
        return recurso;
    }
}