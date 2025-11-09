package com.lealtixservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] stripeOrigins = new String[] {"https://checkout.stripe.com", "https://js.stripe.com"};
                String[] localDevOrigins = new String[] {"http://localhost:4201", "http://localhost:4200", "http://localhost:5173", "http://localhost:8080"};

                Set<String> originsSet = new LinkedHashSet<>();
                if (allowedOrigins != null && !allowedOrigins.trim().isEmpty()) {
                    String[] provided = allowedOrigins.split("\\s*,\\s*");
                    originsSet.addAll(Arrays.asList(provided));
                }
                originsSet.addAll(Arrays.asList(stripeOrigins));
                originsSet.addAll(Arrays.asList(localDevOrigins));

                String[] origins = originsSet.toArray(new String[0]);

                // Aplicar a API pública del frontend
                registry.addMapping("/api/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true)
                        .maxAge(3600);

                registry.addMapping("/stripe/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Content-Type")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        String[] stripeOrigins = new String[] {"https://checkout.stripe.com", "https://js.stripe.com"};
        String[] localDevOrigins = new String[] {"http://localhost:4201", "http://localhost:4200", "http://localhost:5173", "http://localhost:8080"};

        Set<String> originsSet = new LinkedHashSet<>();
        if (allowedOrigins != null && !allowedOrigins.trim().isEmpty()) {
            String[] provided = allowedOrigins.split("\\s*,\\s*");
            originsSet.addAll(Arrays.asList(provided));
        }
        originsSet.addAll(Arrays.asList(stripeOrigins));
        originsSet.addAll(Arrays.asList(localDevOrigins));

        List<String> origins = List.copyOf(originsSet);

        CorsConfiguration apiConfig = new CorsConfiguration();
        apiConfig.setAllowedOrigins(origins);
        apiConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        apiConfig.setAllowedHeaders(Arrays.asList("*"));
        apiConfig.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        apiConfig.setAllowCredentials(true);
        apiConfig.setMaxAge(3600L);

        CorsConfiguration stripeConfig = new CorsConfiguration();
        stripeConfig.setAllowedOrigins(origins);
        stripeConfig.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        stripeConfig.setAllowedHeaders(Arrays.asList("*"));
        stripeConfig.setExposedHeaders(Arrays.asList("Content-Type"));
        stripeConfig.setAllowCredentials(false);
        stripeConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", apiConfig);
        source.registerCorsConfiguration("/stripe/**", stripeConfig);
        return source;
    }
}
