package com.lealtixservice.service.impl;

import com.lealtixservice.dto.CampaignResultDTO;
import com.lealtixservice.entity.Campaign;
import com.lealtixservice.entity.CampaignResult;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.mapper.CampaignMapper;
import com.lealtixservice.repository.CampaignRepository;
import com.lealtixservice.repository.CampaignResultRepository;
import com.lealtixservice.service.CampaignResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignResultServiceImpl implements CampaignResultService {

    private final CampaignResultRepository resultRepository;
    private final CampaignRepository campaignRepository;

    @Override
    public CampaignResultDTO findByCampaignId(Long campaignId) {
        CampaignResult result = ensureResult(campaignId);
        return CampaignMapper.toResultDTO(result);
    }

    @Override
    @Transactional
    public CampaignResultDTO incrementViews(Long campaignId) {
        CampaignResult result = ensureResult(campaignId);
        result.registerView();
        CampaignResult saved = resultRepository.save(result);
        return CampaignMapper.toResultDTO(saved);
    }

    @Override
    @Transactional
    public CampaignResultDTO incrementClicks(Long campaignId) {
        CampaignResult result = ensureResult(campaignId);
        result.registerClick();
        CampaignResult saved = resultRepository.save(result);
        return CampaignMapper.toResultDTO(saved);
    }

    @Override
    @Transactional
    public CampaignResultDTO incrementRedemptions(Long campaignId) {
        CampaignResult result = ensureResult(campaignId);
        result.registerRedemption();
        CampaignResult saved = resultRepository.save(result);
        return CampaignMapper.toResultDTO(saved);
    }

    private CampaignResult ensureResult(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign no encontrada id=" + campaignId));
        CampaignResult existing = resultRepository.findByCampaignId(campaignId);
        if (existing != null) return existing;
        CampaignResult created = CampaignResult.builder()
                .campaign(campaign)
                .views(0)
                .clicks(0)
                .redemptions(0)
                .build();
        return resultRepository.save(created);
    }
}

