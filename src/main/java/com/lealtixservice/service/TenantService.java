package com.lealtixservice.service;

import com.lealtixservice.entity.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantService {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(Long id);
    List<Tenant> findAll();
    void deleteById(Long id);
}

