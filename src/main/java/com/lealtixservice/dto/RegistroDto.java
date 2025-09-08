package com.lealtixservice.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegistroDto {
    // Datos de usuario (admin)
    private String nombre;
    private String paterno;
    private String materno;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String email;
    private String password;

    // Datos del negocio (tenant)
    private String nombreNegocio;
    private String direccion;
    private String telefonoNegocio;
    private String tipoNegocio;

    // Datos de pago (simulado)
    private String plan;
    private String status;

    // token
    private String token;
}

