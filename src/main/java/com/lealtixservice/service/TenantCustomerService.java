package com.lealtixservice.service;

import com.lealtixservice.entity.TenantCustomer;

import java.util.List;
import java.util.Optional;

public interface TenantCustomerService {
    TenantCustomer save(TenantCustomer customer);
    Optional<TenantCustomer> findById(Long id);
    List<TenantCustomer> findAll();
    void deleteById(Long id);
    List<TenantCustomer> findByTenantId(Long tenantId);
}

