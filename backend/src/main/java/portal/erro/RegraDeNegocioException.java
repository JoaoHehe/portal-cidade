package portal.erro;

import org.springframework.http.HttpStatus;

// Exceção lançada quando uma regra do sistema é violada
// (ex: e-mail já cadastrado). Carrega a mensagem e o código HTTP da resposta.
public class RegraDeNegocioException extends RuntimeException {

    private final HttpStatus status;

    // Se ninguém disser o status, usa 400 (requisição inválida)
    public RegraDeNegocioException(String mensagem) {
        this(mensagem, HttpStatus.BAD_REQUEST);
    }

    public RegraDeNegocioException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}