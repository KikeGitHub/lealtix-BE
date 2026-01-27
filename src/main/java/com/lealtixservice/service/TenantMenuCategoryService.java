package com.lealtixservice.service;

import com.lealtixservice.dto.CategoryDTO;
import com.lealtixservice.dto.ReorderCategoryRequest;
import com.lealtixservice.dto.TenantMenuCategoryDTO;
import com.lealtixservice.dto.TenantMenuProductDTO;
import com.lealtixservice.entity.TenantMenuCategory;
import java.util.List;
import java.util.Optional;

public interface TenantMenuCategoryService {
    TenantMenuCategory save(TenantMenuCategory category);
    Optional<TenantMenuCategory> findById(Long id);
    List<TenantMenuCategory> findAll();
    void deleteById(Long id);

    List<TenantMenuProductDTO> getByTenantId(Long tenantId);

    TenantMenuCategoryDTO createCategoryProduct(TenantMenuCategoryDTO category);

    List<CategoryDTO> getCategoriesByTenantId(Long tenantId);

    void reorderCategories(Long tenantId, List<ReorderCategoryRequest> reorderRequests);

    /**
     * Busca una categoría por nombre (con normalización case-insensitive y tolerante a variaciones)
     * o la crea si no existe.
     *
     * @param tenantId ID del tenant
     * @param categoryName nombre de la categoría
     * @return categoría encontrada o creada
     */
    TenantMenuCategory findOrCreateByNameNormalized(Long tenantId, String categoryName);
}

