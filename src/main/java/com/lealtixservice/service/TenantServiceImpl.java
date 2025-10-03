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
        if(tenantDto != null) {
            AppUser user = appUserService.findById(tenantDto.getUserId()).orElse(null);
            var existingTenantOpt = tenantRepository.findByAppUserId(user.getId());
            Tenant tenant = null;
            if(existingTenantOpt.isPresent()){
                tenant = existingTenantOpt.get();
                tenant.setNombreNegocio(tenantDto.getNombreNegocio());
                tenant.setDireccion(tenantDto.getDireccion());
                tenant.setTelefono(tenantDto.getTelefono());
                tenant.setTipoNegocio(tenantDto.getTipoNegocio());
                tenant.setSlogan(tenantDto.getSlogan());
                tenant.setLogoUrl(tenantDto.getLogoUrl());
                tenant.setUpdatedAt(LocalDateTime.now());
                tenant.setSchedules(tenantDto.getSchedules());
            }else{
                tenant = Tenant.builder()
                    .nombreNegocio(tenantDto.getNombreNegocio())
                    .direccion(tenantDto.getDireccion())
                    .telefono(tenantDto.getTelefono())
                    .tipoNegocio(tenantDto.getTipoNegocio())
                    .slogan(tenantDto.getSlogan() != null ? tenantDto.getSlogan() : "")
                    .logoUrl(tenantDto.getLogoUrl() != null ? StringUtils.decryptBase64(tenantDto.getLogoUrl()) : "")
                    .isActive(true)
                    .appUser(user)
                    .schedules(tenantDto.getSchedules())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            }

            Tenant savedTenant = tenantRepository.save(tenant);
            // Actualiza slug y UIDTenant después de obtener el ID
            String slug = StringUtils.createSlug(savedTenant.getNombreNegocio(), savedTenant.getId());
            String uidTenant = "UID-" + savedTenant.getId();
            savedTenant.setSlug(slug);
            savedTenant.setUIDTenant(uidTenant);
            return tenantRepository.save(savedTenant);

        }
        return null;
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
