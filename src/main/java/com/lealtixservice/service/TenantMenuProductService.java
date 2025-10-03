package com.lealtixservice.service;

import com.lealtixservice.entity.TenantMenuProduct;
import java.util.List;
import java.util.Optional;

public interface TenantMenuProductService {
    TenantMenuProduct save(TenantMenuProduct product);
    Optional<TenantMenuProduct> findById(Long id);
    List<TenantMenuProduct> findAll();
    void deleteById(Long id);
}

