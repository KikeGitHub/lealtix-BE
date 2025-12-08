package com.lealtixservice.repository;

import com.lealtixservice.entity.CampaignResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignResultRepository extends JpaRepository<CampaignResult, Long> {
    CampaignResult findByCampaignId(Long campaignId);
}

