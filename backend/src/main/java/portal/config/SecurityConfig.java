package portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protege sites com sessão/cookie. Nossa API vai usar token (JWT),
                // então desligamos.
                .csrf(csrf -> csrf.disable())

                // STATELESS = o servidor não guarda "quem está logado". Cada
                // requisição prova quem é com o token.
                .sessionManagement(sessao ->
                        sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // POR ENQUANTO libera tudo, só para nada quebrar.
                // Vamos trancar nos próximos passos.
                .authorizeHttpRequests(regras -> regras.anyRequest().permitAll());

        return http.build();
    }
}