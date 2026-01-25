package com.lealtixservice.service;

import com.lealtixservice.dto.BulkProductRequest;
import com.lealtixservice.dto.BulkProductResponse;
import com.lealtixservice.dto.TenantMenuCategoryDTO;
import com.lealtixservice.dto.TenantMenuProductDTO;
import com.lealtixservice.entity.TenantMenuProduct;
import java.util.List;
import java.util.Optional;

public interface TenantMenuProductService {
    TenantMenuProduct save(TenantMenuProduct product);
    Optional<TenantMenuProduct> findById(Long id);
    List<TenantMenuProduct> findAll();
    void deleteById(Long id);

    TenantMenuProductDTO create(TenantMenuProductDTO product);

    List<TenantMenuProductDTO>  getProductsByTenantId(Long tenantId);

    /**
     * Crea múltiples productos en una sola operación.
     * Utiliza búsqueda normalizada de categorías para evitar duplicados.
     *
     * @param bulkRequest solicitud con lista de productos a crear
     * @return respuesta con resumen de la operación
     */
    BulkProductResponse createBulk(BulkProductRequest bulkRequest);
}

