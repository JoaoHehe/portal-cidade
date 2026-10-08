package portal.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import portal.usuario.Usuario;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long horasValidade;

    public TokenService(JwtEncoder jwtEncoder,
                        @Value("${app.jwt.horas-validade}") long horasValidade) {
        this.jwtEncoder = jwtEncoder;
        this.horasValidade = horasValidade;
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();

        // "Claims" = as informações guardadas DENTRO do token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("portal-cidadao")
                .subject(usuario.getEmail())      // "dono" do token
                .issuedAt(agora)                  // quando foi criado
                .expiresAt(agora.plus(horasValidade, ChronoUnit.HOURS)) // validade
                .claim("id", usuario.getId())
                .claim("perfil", usuario.getPerfil().name())
                .build();

        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(cabecalho, claims))
                .getTokenValue();
    }
}