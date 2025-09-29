package com.lealtixservice.util;

import com.lealtixservice.dto.AppUserDTO;
import com.lealtixservice.dto.TenantDTO;
import com.lealtixservice.dto.TenantUserDTO;
import com.lealtixservice.dto.TenantUserIdDTO;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantUser;
import com.lealtixservice.entity.TenantUserId;

public class TenantUserMapper {
    public static TenantUserDTO toDTO(TenantUser entity) {
        if (entity == null) return null;
        return TenantUserDTO.builder()
                .id(TenantUserIdDTO.builder()
                        .tenantId(entity.getId().getTenantId())
                        .userId(entity.getId().getUserId())
                        .roleId(entity.getId().getRoleId())
                        .build())
                .tenant(toTenantDTO(entity.getTenant()))
                .user(toAppUserDTO(entity.getUser()))
                .roleId(entity.getRole() != null ? entity.getRole().getId() : null)
                .build();
    }

    public static TenantUser toEntity(TenantUserDTO dto) {
        if (dto == null) return null;
        TenantUserId id = new TenantUserId(
                dto.getId().getTenantId(),
                dto.getId().getUserId(),
                dto.getId().getRoleId()
        );
        TenantUser.TenantUserBuilder builder = TenantUser.builder().id(id);
        if (dto.getTenant() != null) {
            builder.tenant(toTenant(dto.getTenant()));
        }
        if (dto.getUser() != null) {
            builder.user(toAppUser(dto.getUser()));
        }
        // No se mapea Role completo, solo el id
        return builder.build();
    }

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
                .isActive(tenant.isActive())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }

    public static Tenant toTenant(TenantDTO dto) {
        if (dto == null) return null;
        return Tenant.builder()
                .id(dto.getId())
                .nombreNegocio(dto.getNombreNegocio())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .tipoNegocio(dto.getTipoNegocio())
                .slug(dto.getSlug())
                .UIDTenant(dto.getUIDTenant())
                .isActive(dto.isActive())
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
                .password(user.getPasswordHash())
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
}

