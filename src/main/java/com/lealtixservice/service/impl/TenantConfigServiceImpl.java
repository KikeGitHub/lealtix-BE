package com.lealtixservice.service.impl;

import com.lealtixservice.dto.TenantConfigDTO;
import com.lealtixservice.entity.TenantConfig;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.repository.TenantConfigRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.service.TenantConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantConfigServiceImpl implements TenantConfigService {

    private final TenantConfigRepository tenantConfigRepository;
    private final TenantRepository tenantRepository;

    @Override
    public TenantConfigDTO saveTenantConfig(TenantConfigDTO dto) {
        Tenant tenant = tenantRepository.findById(dto.getTenantId()).orElse(null);
        if (tenant == null) return null;
        TenantConfig entity = toEntity(dto, tenant);
        TenantConfig saved = tenantConfigRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public TenantConfigDTO getTenantConfigById(Long id) {
        return tenantConfigRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public List<TenantConfigDTO> getTenantConfigsByTenantId(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant == null) return List.of();
        return tenantConfigRepository.findByTenant(tenant)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private TenantConfigDTO toDTO(TenantConfig entity) {
        return TenantConfigDTO.builder()
                .id(entity.getId())
                .tenantId(entity.getTenant().getId())
                .tipoNegocio(entity.getTipoNegocio())
                .since(entity.getSince())
                .imgLogo(entity.getImgLogo())
                .story(entity.getStory())
                .vision(entity.getVision())
                .listVision(entity.getListVision())
                .bussinesEmail(entity.getBussinesEmail())
                .twitter(entity.getTwitter())
                .facebook(entity.getFacebook())
                .linkedin(entity.getLinkedin())
                .instagram(entity.getInstagram())
                .tiktok(entity.getTiktok())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .schedules(entity.getSchedules() != null ? entity.getSchedules().stream()
                    .map(s -> TenantConfigDTO.ScheduleDTO.builder().day(s.getDay()).hours(s.getHours()).build())
                    .collect(Collectors.toList()) : null)
                .build();
    }

    private TenantConfig toEntity(TenantConfigDTO dto, Tenant tenant) {
        TenantConfig entity = TenantConfig.builder()
                .id(dto.getId())
                .tenant(tenant)
                .tipoNegocio(dto.getTipoNegocio())
                .since(dto.getSince())
                .imgLogo(dto.getImgLogo())
                .story(dto.getStory())
                .vision(dto.getVision())
                .listVision(dto.getListVision())
                .bussinesEmail(dto.getBussinesEmail())
                .twitter(dto.getTwitter())
                .facebook(dto.getFacebook())
                .linkedin(dto.getLinkedin())
                .instagram(dto.getInstagram())
                .tiktok(dto.getTiktok())
                .build();
        if (dto.getSchedules() != null) {
            entity.setSchedules(dto.getSchedules().stream()
                .map(s -> new TenantConfig.Schedule(s.getDay(), s.getHours()))
                .collect(Collectors.toList()));
        }
        return entity;
    }
}

