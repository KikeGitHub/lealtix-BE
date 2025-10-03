package com.lealtixservice.service.impl;

import com.lealtixservice.dto.TenantConfigDTO;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantConfig;
import com.lealtixservice.repository.TenantConfigRepository;
import com.lealtixservice.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TenantConfigServiceImplTest {
    @Mock
    private TenantConfigRepository tenantConfigRepository;
    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private TenantConfigServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTenantConfig_success() {
        Tenant tenant = Tenant.builder().id(2L).build();
        TenantConfigDTO dto = TenantConfigDTO.builder().tenantId(2L).tipoNegocio("Negocio").build();
        TenantConfig entity = TenantConfig.builder().id(1L).tenant(tenant).tipoNegocio("Negocio").build();
        when(tenantRepository.findById(2L)).thenReturn(Optional.of(tenant));
        when(tenantConfigRepository.save(any())).thenReturn(entity);
        TenantConfigDTO result = service.saveTenantConfig(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Negocio", result.getTipoNegocio());
    }

    @Test
    void saveTenantConfig_tenantNotFound() {
        TenantConfigDTO dto = TenantConfigDTO.builder().tenantId(99L).build();
        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());
        TenantConfigDTO result = service.saveTenantConfig(dto);
        assertNull(result);
    }

    @Test
    void getTenantConfigById_found() {
        Tenant tenant = Tenant.builder().id(2L).build();
        TenantConfig entity = TenantConfig.builder().id(1L).tenant(tenant).build();
        when(tenantConfigRepository.findById(1L)).thenReturn(Optional.of(entity));
        TenantConfigDTO result = service.getTenantConfigById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTenantConfigById_notFound() {
        when(tenantConfigRepository.findById(99L)).thenReturn(Optional.empty());
        TenantConfigDTO result = service.getTenantConfigById(99L);
        assertNull(result);
    }

    @Test
    void getTenantConfigsByTenantId_found() {
        Tenant tenant = Tenant.builder().id(2L).build();
        TenantConfig entity1 = TenantConfig.builder().id(1L).tenant(tenant).build();
        TenantConfig entity2 = TenantConfig.builder().id(2L).tenant(tenant).build();
        when(tenantRepository.findById(2L)).thenReturn(Optional.of(tenant));
        when(tenantConfigRepository.findByTenant(tenant)).thenReturn(List.of(entity1, entity2));
        List<TenantConfigDTO> result = service.getTenantConfigsByTenantId(2L);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getTenantConfigsByTenantId_tenantNotFound() {
        when(tenantRepository.findById(99L)).thenReturn(Optional.empty());
        List<TenantConfigDTO> result = service.getTenantConfigsByTenantId(99L);
        assertTrue(result.isEmpty());
    }
}

