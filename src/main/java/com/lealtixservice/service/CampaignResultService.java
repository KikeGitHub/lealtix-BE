package com.lealtixservice.service;

import com.lealtixservice.dto.CampaignResultDTO;

public interface CampaignResultService {
    CampaignResultDTO findByCampaignId(Long campaignId);
    CampaignResultDTO incrementViews(Long campaignId);
    CampaignResultDTO incrementClicks(Long campaignId);
    CampaignResultDTO incrementRedemptions(Long campaignId);
}

