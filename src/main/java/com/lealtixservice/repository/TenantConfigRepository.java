package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantConfig;
import com.lealtixservice.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {
    List<TenantConfig> findByTenant(Tenant tenant);
    TenantConfig findByTenantId(Long tenantId);
}

