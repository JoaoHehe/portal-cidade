package portal.erro;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice = "se qualquer controller lançar um erro, venha parar aqui".
// Centraliza o tratamento de erros em um lugar só.
@RestControllerAdvice
public class TratadorDeErros {

    // Caso 1: uma regra de negócio foi violada (ex: e-mail duplicado)
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        ErroResponse corpo = new ErroResponse(
                ex.getStatus().value(), ex.getMessage(), Map.of());
        return ResponseEntity.status(ex.getStatus()).body(corpo);
    }

    // Caso 2: os dados enviados não passaram nas validações (@NotBlank, @Email...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        // Monta um mapa tipo { "email": "E-mail inválido", "senha": "..." }
        Map<String, String> campos = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }

        ErroResponse corpo = new ErroResponse(400, "Dados inválidos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }
}