package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la creación masiva de productos.
 * Agrupa una lista de productos con información común del tenant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkProductRequest {

    private Long tenantId;
    private List<TenantMenuProductDTO> products;
}
