package com.lealtixservice.repository;

import com.lealtixservice.entity.CampaignTemplate;
import com.lealtixservice.enums.PromoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignTemplateRepository extends JpaRepository<CampaignTemplate, Long> {
    List<CampaignTemplate> findByIsActiveTrue();
    List<CampaignTemplate> findByCategory(String category);
    List<CampaignTemplate> findByDefaultPromoType(PromoType promoType);
}

