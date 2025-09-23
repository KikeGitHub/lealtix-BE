package com.lealtixservice.service;

import com.lealtixservice.dto.TenantConfigDTO;
import java.util.List;

public interface TenantConfigService {
    TenantConfigDTO saveTenantConfig(TenantConfigDTO tenantConfigDTO);
    TenantConfigDTO getTenantConfigById(Long id);
    List<TenantConfigDTO> getTenantConfigsByTenantId(Long tenantId);
}

