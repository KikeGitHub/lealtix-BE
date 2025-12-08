package com.lealtixservice.service.impl;

import com.lealtixservice.dto.*;
import com.lealtixservice.entity.Campaign;
import com.lealtixservice.entity.CampaignTemplate;
import com.lealtixservice.enums.CampaignStatus;
import com.lealtixservice.enums.PromoType;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.mapper.CampaignMapper;
import com.lealtixservice.repository.CampaignRepository;
import com.lealtixservice.repository.CampaignTemplateRepository;
import com.lealtixservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignServiceImpl.class);

    private final CampaignRepository campaignRepository;
    private final CampaignTemplateRepository templateRepository;

    @Override
    @Transactional
    public CampaignResponse create(CreateCampaignRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());
        CampaignTemplate template = null;
        if (request.getTemplateId() != null) {
            template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("CampaignTemplate no encontrado id=" + request.getTemplateId()));
        }
        Campaign entity = CampaignMapper.toEntity(request, template);
        Campaign saved = campaignRepository.save(entity);
        return CampaignMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CampaignResponse update(Long id, UpdateCampaignRequest request) {
        long t0 = System.currentTimeMillis();
        log.debug("update() start - id={} request={}", id, request);

        Campaign campaign = campaignRepository.findByIdWithTemplate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign no encontrada id=" + id));
        long t1 = System.currentTimeMillis();
        log.debug("Fetched campaign id={} (fetchTime={}ms)", id, (t1 - t0));

        // Loguear campos clave para ver qué se ha cargado.
        try {
            Long templateId = campaign.getTemplate() != null ? campaign.getTemplate().getId() : null;
            log.debug("Campaign loaded: id={}, businessId={}, status={}, isDraft={}, templateId={}, createdAt={}, updatedAt={}",
                    campaign.getId(), campaign.getBusinessId(), campaign.getStatus(), campaign.getIsDraft(), templateId,
                    campaign.getCreatedAt(), campaign.getUpdatedAt());
        } catch (Exception e) {
            log.debug("Error al leer propiedades de campaign en debug: {}", e.getMessage());
        }

        if (request.getStartDate() != null || request.getEndDate() != null) {
            LocalDate start = request.getStartDate() != null ? request.getStartDate() : campaign.getStartDate();
            LocalDate end = request.getEndDate() != null ? request.getEndDate() : campaign.getEndDate();
            validateDates(start, end);
        }
        long t2 = System.currentTimeMillis();
        log.debug("Validation done (time since fetch={}ms)", (t2 - t1));

        CampaignMapper.updateEntityFromRequest(request, campaign);
        long t3 = System.currentTimeMillis();
        log.debug("Mapping done (mappingTime={}ms)", (t3 - t2));

        Campaign saved = campaignRepository.save(campaign);
        long t4 = System.currentTimeMillis();
        log.debug("Saved campaign id={} (saveTime={}ms totalTime={}ms)", saved.getId(), (t4 - t3), (t4 - t0));

        return CampaignMapper.toResponse(saved);
    }

    @Override
    public CampaignResponse findById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign no encontrada id=" + id));
        return CampaignMapper.toResponse(campaign);
    }

    @Override
    public List<CampaignResponse> findByBusinessId(Long businessId) {
        return campaignRepository.findByBusinessId(businessId).stream()
                .map(CampaignMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign no encontrada id=" + id));
        campaignRepository.delete(campaign);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("endDate debe ser posterior a startDate");
        }
    }

    @Override
    @Transactional
    public CampaignResponse createDraft(CreateCampaignDraftDto dto) {
        CampaignTemplate template = null;
        if (dto.getTemplateId() != null) {
            template = templateRepository.findById(dto.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("CampaignTemplate no encontrado id=" + dto.getTemplateId()));
        }

        Campaign campaign = Campaign.builder()
                .template(template)
                .businessId(dto.getBusinessId())
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .promoType(dto.getPromoType() != null ? PromoType.valueOf(dto.getPromoType()) : null)
                .promoValue(dto.getPromoValue())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(CampaignStatus.DRAFT)
                .callToAction(dto.getCallToAction())
                .channels(dto.getChannels() != null ? String.join(",", dto.getChannels()) : null)
                .segmentation(dto.getSegmentation() != null ? String.join(",", dto.getSegmentation()) : null)
                .isAutomatic(dto.getIsAutomatic() != null ? dto.getIsAutomatic() : false)
                .isDraft(true)
                .build();

        Campaign saved = campaignRepository.save(campaign);
        return CampaignMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CampaignResponse updateDraft(Long id, CreateCampaignDraftDto dto) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign no encontrada id=" + id));

        if (!campaign.getIsDraft()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La campaña no es un borrador");
        }

        // Validar fechas si se proporcionan
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            validateDates(dto.getStartDate(), dto.getEndDate());
        }

        // Actualizar campos
        campaign.setTitle(dto.getTitle());
        campaign.setSubtitle(dto.getSubtitle());
        campaign.setDescription(dto.getDescription());
        campaign.setImageUrl(dto.getImageUrl());
        campaign.setPromoType(dto.getPromoType() != null ? PromoType.valueOf(dto.getPromoType()) : null);
        campaign.setPromoValue(dto.getPromoValue());
        campaign.setStartDate(dto.getStartDate());
        campaign.setEndDate(dto.getEndDate());
        campaign.setCallToAction(dto.getCallToAction());
        campaign.setChannels(dto.getChannels() != null ? String.join(",", dto.getChannels()) : null);
        campaign.setSegmentation(dto.getSegmentation() != null ? String.join(",", dto.getSegmentation()) : null);
        campaign.setIsAutomatic(dto.getIsAutomatic() != null ? dto.getIsAutomatic() : false);

        Campaign saved = campaignRepository.save(campaign);
        return CampaignMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CampaignResponse publishDraft(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign no encontrada id=" + id));

        if (!campaign.getIsDraft()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La campaña no es un borrador");
        }

        // Validaciones para publicación
        List<String> errors = new ArrayList<>();

        if (campaign.getTitle() == null || campaign.getTitle().length() < 3) {
            errors.add("El título debe tener al menos 3 caracteres");
        }

        if (campaign.getDescription() == null || campaign.getDescription().length() < 10) {
            errors.add("La descripción debe tener al menos 10 caracteres");
        }

        if (campaign.getStartDate() == null || campaign.getStartDate().isBefore(LocalDate.now())) {
            errors.add("La fecha de inicio debe ser presente o futura");
        }

        if (campaign.getEndDate() == null ||
            (campaign.getStartDate() != null && campaign.getEndDate().isBefore(campaign.getStartDate()))) {
            errors.add("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        if (campaign.getChannels() == null || campaign.getChannels().trim().isEmpty()) {
            errors.add("Debe especificar al menos un canal");
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Errores de validación: " + String.join(", ", errors));
        }

        // Publicar campaña
        campaign.setIsDraft(false);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setPublishedAt(LocalDateTime.now());

        Campaign saved = campaignRepository.save(campaign);
        return CampaignMapper.toResponse(saved);
    }

    @Override
    public List<CampaignResponse> getDraftsByBusiness(Long businessId) {
        return campaignRepository.findByBusinessIdAndIsDraftOrderByUpdatedAtDesc(businessId, true)
                .stream()
                .map(CampaignMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CampaignResponse> getActiveCampaigns(Long businessId) {
        return campaignRepository.findByBusinessIdAndIsDraftFalseOrderByCreatedAtDesc(businessId)
                .stream()
                .map(CampaignMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CampaignResponse create(CreateCampaignDto dto) {
        // Validar fechas
        validateDates(dto.getStartDate(), dto.getEndDate());

        CampaignTemplate template = null;
        if (dto.getTemplateId() != null) {
            template = templateRepository.findById(dto.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("CampaignTemplate no encontrado id=" + dto.getTemplateId()));
        }

        Campaign campaign = Campaign.builder()
                .template(template)
                .businessId(dto.getBusinessId())
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .promoType(dto.getPromoType() != null ? PromoType.valueOf(dto.getPromoType()) : null)
                .promoValue(dto.getPromoValue())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(CampaignStatus.ACTIVE)
                .callToAction(dto.getCallToAction())
                .channels(dto.getChannels() != null ? String.join(",", dto.getChannels()) : null)
                .segmentation(dto.getSegmentation() != null ? String.join(",", dto.getSegmentation()) : null)
                .isAutomatic(dto.getIsAutomatic() != null ? dto.getIsAutomatic() : false)
                .isDraft(false)
                .publishedAt(LocalDateTime.now())
                .build();

        Campaign saved = campaignRepository.save(campaign);
        return CampaignMapper.toResponse(saved);
    }
}
