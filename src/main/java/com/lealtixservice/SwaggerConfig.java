package com.lealtixservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Lealtix Service API",
                version = "1.0",
                description = "Documentación de la API para el servicio de pre-registro e invitaciones de Lealtix."
        )
)
public class SwaggerConfig {
    // Configuración personalizada de OpenAPI si se requiere
}

