package com.lealtixservice.service;

import com.lealtixservice.dto.CategoryDTO;
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
}

