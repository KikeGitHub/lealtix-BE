package com.lealtixservice.service;

import com.lealtixservice.dto.*;
import java.util.List;

public interface CampaignService {
    CampaignResponse create(CreateCampaignRequest request);
    CampaignResponse update(Long id, UpdateCampaignRequest request);
    CampaignResponse findById(Long id);
    List<CampaignResponse> findByBusinessId(Long businessId);
    void delete(Long id);

    // Métodos para borradores
    CampaignResponse createDraft(CreateCampaignDraftDto dto);
    CampaignResponse updateDraft(Long id, CreateCampaignDraftDto dto);
    CampaignResponse publishDraft(Long id);
    CampaignResponse create(CreateCampaignDto dto);
    List<CampaignResponse> getDraftsByBusiness(Long businessId);
    List<CampaignResponse> getActiveCampaigns(Long businessId);
}

