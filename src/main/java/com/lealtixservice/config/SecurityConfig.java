package com.lealtixservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Usar la configuración CORS central
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // CSRF: habilitado por defecto, pero ignorar webhooks y endpoints públicos que reciben POST
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/api/stripe/webhook", "POST"),
                        new AntPathRequestMatcher("/api/preregistro", "POST"),
                        new AntPathRequestMatcher("/api/tenant-payment/create-payment-intent", "POST")
                ))

                // Deshabilitar formLogin y httpBasic para evitar redirects/popups en endpoints públicos
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Reglas de autorización
                .authorizeHttpRequests(authz -> authz
                        // Permitir preflight CORS (OPTIONS requests)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Raíz y recursos estáticos
                        .requestMatchers("/", "/favicon.ico", "/robots.txt").permitAll()

                        // Documentación Swagger y error endpoint
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/error"
                        ).permitAll()

                        // Endpoint público: crear payment intent (frontend público)
                        .requestMatchers(HttpMethod.POST, "/api/tenant-payment/create-payment-intent").permitAll()

                        // Endpoint público: preregistro (frontend público)
                        .requestMatchers(HttpMethod.POST, "/api/preregistro").permitAll()

                        // Endpoint público: webhook de Stripe (seguridad por firma en controlador)
                        .requestMatchers(HttpMethod.POST, "/api/stripe/webhook").permitAll()

                        // El resto requiere autenticación
                        .anyRequest().authenticated()
                )

                // Manejo de excepciones: retornar 401 en lugar de 403 para requests sin auth
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
