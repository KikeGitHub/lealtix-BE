package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la entidad Tenant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantDTO {
    private Long id;
    private Long userId;
    private String nombreNegocio;
    private String direccion;
    private String telefono;
    private String tipoNegocio;
    private String slug;
    private String logoUrl;
    private String slogan;
    private String UIDTenant;
    private boolean isActive;
    private String schedules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}

