package com.lealtixservice.util;

import com.lealtixservice.dto.*;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantConfig;

public class TenantUserMapper {

    public static TenantDTO toTenantDTO(Tenant tenant) {
        if (tenant == null) return null;
        return TenantDTO.builder()
                .id(tenant.getId())
                .nombreNegocio(tenant.getNombreNegocio())
                .direccion(tenant.getDireccion())
                .telefono(tenant.getTelefono())
                .tipoNegocio(tenant.getTipoNegocio())
                .slug(tenant.getSlug())
                .UIDTenant(tenant.getUIDTenant())
                .slogan(tenant.getSlogan())
                .logoUrl(tenant.getLogoUrl())
                .isActive(tenant.isActive())
                .schedules(tenant.getSchedules())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }

    public static Tenant toTenant(TenantDTO dto) {
        if (dto == null) return null;
        return Tenant.builder()
                .nombreNegocio(dto.getNombreNegocio())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .tipoNegocio(dto.getTipoNegocio())
                .slug(dto.getSlug())
                .UIDTenant(dto.getUIDTenant())
                .isActive(true)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static AppUserDTO toAppUserDTO(AppUser user) {
        if (user == null) return null;
        return AppUserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .fechaNacimiento(user.getFechaNacimiento())
                .telefono(user.getTelefono())
                .email(user.getEmail())
                .password(EncrypUtils.decrypPassword(user.getPasswordHash()))
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static AppUser toAppUser(AppUserDTO dto) {
        if (dto == null) return null;
        return AppUser.builder()
                .id(dto.getId())
                .fullName(dto.getFullName())
                .fechaNacimiento(dto.getFechaNacimiento())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .passwordHash(dto.getPassword())
                .isActive(dto.isActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static TenantConfigDTO toTenantConfigDTO(TenantConfig tenantConfig) {
        if (tenantConfig == null) return null;
        return TenantConfigDTO.builder()
                .id(tenantConfig.getId())
                .tenantId(tenantConfig.getTenant().getId())
                .history(tenantConfig.getHistory())
                .vision(tenantConfig.getVision())
                .bussinesEmail(tenantConfig.getBussinesEmail())
                .twitter(tenantConfig.getTwitter())
                .facebook(tenantConfig.getFacebook())
                .linkedin(tenantConfig.getLinkedin())
                .instagram(tenantConfig.getInstagram())
                .tiktok(tenantConfig.getTiktok())
                .createdAt(tenantConfig.getCreatedAt())
                .updatedAt(tenantConfig.getUpdatedAt())
                .build();
    }
}

