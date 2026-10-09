package portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// Classe de configuração: aqui a gente ensina o Spring Security como se comportar
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Embaralha a senha antes de salvar no banco (BCrypt).
    // Ex: "123456" vira "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
    // É um caminho só de ida: não dá para "desembaralhar" de volta.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define as regras de acesso da API
    // Define as regras de acesso da API
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Nossa API usa token (JWT), não cookie de sessão, então desligamos o CSRF
                .csrf(csrf -> csrf.disable())

                // STATELESS = o servidor não guarda "quem está logado".
                // Cada requisição prova quem é com o token.
                .sessionManagement(sessao ->
                        sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(regras -> regras
                        // Qualquer pessoa pode: testar a API, se cadastrar e fazer login
                        .requestMatchers("/api/ping", "/api/auth/**").permitAll()

                        // Área da prefeitura: só funcionário ou admin
                        .requestMatchers("/api/prefeitura/**").hasAnyRole("FUNCIONARIO", "ADMIN")

                        // Área do cidadão: só quem tem perfil CIDADAO
                        .requestMatchers("/api/ocorrencias/**").hasRole("CIDADAO")

                        // Todo o resto: precisa estar logado (ter um token válido)
                        .anyRequest().authenticated())

                // Liga a conferência do token JWT em toda requisição.
                // Usa o JwtDecoder que criamos no JwtConfig.
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conversorDeToken())));

        return http.build();
    }

    // Ensina o Spring a ler o campo "perfil" do token e transformar em "role".
    // Ex: perfil "CIDADAO" no token vira a autoridade "ROLE_CIDADAO".
    private JwtAuthenticationConverter conversorDeToken() {
        JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
        autoridades.setAuthoritiesClaimName("perfil");
        autoridades.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(autoridades);
        return conversor;
    }
}
