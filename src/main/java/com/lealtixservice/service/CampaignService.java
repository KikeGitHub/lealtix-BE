package com.lealtixservice.service;

import com.lealtixservice.dto.*;
import java.util.List;

public interface CampaignService {
    CampaignResponse create(CreateCampaignRequest request);
    CampaignResponse update(Long id, UpdateCampaignRequest request);
    CampaignResponse findById(Long id);
    List<CampaignResponse> findByBusinessId(Long businessId);
    void delete(Long id);
}

