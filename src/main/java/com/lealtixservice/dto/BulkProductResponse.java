package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la respuesta de creación masiva de productos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkProductResponse {

    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private List<TenantMenuProductDTO> createdProducts;
    private List<String> errors;
}
