package com.lealtixservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationDto {
    private String token;
    private String email;
    private String nombreNegocio;
    private String datosGenerales;
    private String datosPagoMinimos;

}

