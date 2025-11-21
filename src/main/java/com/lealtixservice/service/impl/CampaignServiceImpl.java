package com.lealtixservice.service.impl;

import com.lealtixservice.dto.*;
import com.lealtixservice.entity.Campaign;
import com.lealtixservice.entity.CampaignTemplate;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.mapper.CampaignMapper;
import com.lealtixservice.repository.CampaignRepository;
import com.lealtixservice.repository.CampaignTemplateRepository;
import com.lealtixservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

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
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign no encontrada id=" + id));
        if (request.getStartDate() != null || request.getEndDate() != null) {
            LocalDate start = request.getStartDate() != null ? request.getStartDate() : campaign.getStartDate();
            LocalDate end = request.getEndDate() != null ? request.getEndDate() : campaign.getEndDate();
            validateDates(start, end);
        }
        CampaignMapper.updateEntityFromRequest(request, campaign);
        Campaign saved = campaignRepository.save(campaign);
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
}
