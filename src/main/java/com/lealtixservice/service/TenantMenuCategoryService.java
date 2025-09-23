package com.lealtixservice.service;

import com.lealtixservice.entity.TenantMenuCategory;
import java.util.List;
import java.util.Optional;

public interface TenantMenuCategoryService {
    TenantMenuCategory save(TenantMenuCategory category);
    Optional<TenantMenuCategory> findById(Long id);
    List<TenantMenuCategory> findAll();
    void deleteById(Long id);
}

