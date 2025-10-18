package com.lealtixservice.service;

import com.lealtixservice.dto.TenantDTO;
import com.lealtixservice.dto.TenantWizardDTO;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantConfig;
import com.lealtixservice.repository.TenantConfigRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.util.StringUtils;
import com.lealtixservice.util.TenantUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Autowired
    private AppUserService appUserService;

    @Override
    public Tenant save(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public Optional<Tenant> findById(Long id) {
        return tenantRepository.findById(id);
    }

    @Override
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        tenantRepository.deleteById(id);
    }

    @Override
    public Optional<Tenant> updateTenant(Long id, TenantDTO dto) {
        if(id != null && dto != null) {
            return tenantRepository.findById(id).map(existingTenant -> {
                // Actualiza solo si el DTO trae valor, si no, deja el valor actual
                if (dto.getNombreNegocio() != null && !dto.getNombreNegocio().isBlank()) {
                    existingTenant.setNombreNegocio(dto.getNombreNegocio());
                }
                if (dto.getDireccion() != null && !dto.getDireccion().isBlank()) {
                    existingTenant.setDireccion(dto.getDireccion());
                }
                if (dto.getTelefono() != null && !dto.getTelefono().isBlank()) {
                    existingTenant.setTelefono(dto.getTelefono());
                }
                if (dto.getTipoNegocio() != null && !dto.getTipoNegocio().isBlank()) {
                    existingTenant.setTipoNegocio(dto.getTipoNegocio());
                }
                if (dto.getSlogan() != null && !dto.getSlogan().isBlank()) {
                    existingTenant.setSlogan(dto.getSlogan());
                } else if (existingTenant.getSlogan() == null) {
                    existingTenant.setSlogan(""); // Valor por defecto
                }
                if (dto.getLogoUrl() != null && !dto.getLogoUrl().isBlank()) {
                    String logoUrlDecoded = StringUtils.decryptBase64(dto.getLogoUrl());
                    existingTenant.setLogoUrl(logoUrlDecoded);
                } else if (existingTenant.getLogoUrl() == null) {
                    existingTenant.setLogoUrl(""); // Valor por defecto
                }
                if (dto.getNombreNegocio() != null && !dto.getNombreNegocio().isBlank()) {
                    existingTenant.setSlug(StringUtils.createSlug(dto.getNombreNegocio(), id));
                } else if (existingTenant.getSlug() == null || existingTenant.getSlug().isBlank()) {
                    existingTenant.setSlug(StringUtils.createSlug(existingTenant.getNombreNegocio(), id));
                }
                if (dto.getUIDTenant() != null && !dto.getUIDTenant().isBlank()) {
                    existingTenant.setUIDTenant(dto.getUIDTenant());
                } else if (existingTenant.getUIDTenant() == null) {
                    existingTenant.setUIDTenant("UID-" + id);
                }
                if (existingTenant.getCreatedAt() == null) {
                    existingTenant.setCreatedAt(LocalDateTime.now());
                }
                existingTenant.setUpdatedAt(LocalDateTime.now());
                existingTenant.setActive(true);
                return tenantRepository.save(existingTenant);
            });
        }
        return Optional.empty();
    }

    @Override
    public Tenant create(TenantDTO tenantDto) {
                    if (tenantDto == null) {
                        return null;
                    }

                    Tenant tenant = tenantRepository.findById(tenantDto.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + tenantDto.getId()));

                    TenantConfig tenantConfig = tenantConfigRepository.findByTenantId(tenant.getId());
                    if (tenantConfig == null) {
                        tenantConfig = new TenantConfig();
                        tenantConfig.setTenant(tenant);
                        tenantConfig.setCreatedAt(LocalDateTime.now());
                    }
                    tenantConfig.setUpdatedAt(LocalDateTime.now());
                    tenantConfig.setHistory(tenantDto.getHistory());
                    tenantConfig.setVision(tenantDto.getVision());
                    tenantConfig.setBussinesEmail(tenantDto.getBussinessEmail());
                    // social media
                    tenantConfig.setFacebook(tenantDto.getFacebook());
                    tenantConfig.setInstagram(tenantDto.getInstagram());
                    tenantConfig.setTiktok(tenantDto.getTiktok());
                    tenantConfig.setTwitter(tenantDto.getX());

                    tenantConfigRepository.save(tenantConfig);

                    tenant.setDireccion(tenantDto.getDireccion());
                    tenant.setTelefono(tenantDto.getTelefono());
                    tenant.setTipoNegocio(tenantDto.getTipoNegocio());
                    tenant.setUpdatedAt(LocalDateTime.now());
                    tenant.setSchedules(tenantDto.getSchedules());

                    return tenantRepository.save(tenant);
                }

    @Override
    public TenantWizardDTO getBySlug(String slug) {
        Tenant tenant = tenantRepository.getBySlug(slug).orElse(null);
        if (tenant == null) {
            return null;
        }

        TenantWizardDTO resp = new TenantWizardDTO();
        resp.setTenant(TenantUserMapper.toTenantDTO(tenant));

        if (tenant.getAppUser() != null) {
            resp.setUser(TenantUserMapper.toAppUserDTO(tenant.getAppUser()));
        }

        TenantConfig tenantConfig = tenantConfigRepository.findByTenantId(tenant.getId());
        if (tenantConfig != null) {
            resp.setTenantConfig(TenantUserMapper.toTenantConfigDTO(tenantConfig));
        }

        return resp;
    }
}
