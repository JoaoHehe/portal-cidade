package portal.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

// Monta as duas ferramentas de token:
//  - JwtEncoder: CRIA tokens (assina com a chave secreta)
//  - JwtDecoder: CONFERE tokens (se a assinatura bate, o token é verdadeiro)
@Configuration
public class JwtConfig {

    private final SecretKey chave;

    // @Value pega o valor lá do application.yml (app.jwt.segredo)
    public JwtConfig(@Value("${app.jwt.segredo}") String segredo) {
        this.chave = new SecretKeySpec(
                segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(chave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}