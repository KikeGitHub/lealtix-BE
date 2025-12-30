package com.lealtixservice.repository;

import com.lealtixservice.entity.PromotionReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionRewardRepository extends JpaRepository<PromotionReward, Long> {

    /**
     * Buscar reward por ID de campaña
     */
    Optional<PromotionReward> findByCampaignId(Long campaignId);

    /**
     * Verificar si una campaña tiene reward configurado
     */
    boolean existsByCampaignId(Long campaignId);

    /**
     * Verificar si el límite de uso ha sido alcanzado
     */
    @Query("SELECT CASE WHEN pr.usageLimit IS NULL THEN false " +
           "WHEN pr.usageCount >= pr.usageLimit THEN true " +
           "ELSE false END " +
           "FROM PromotionReward pr WHERE pr.id = :rewardId")
    boolean isUsageLimitReached(@Param("rewardId") Long rewardId);
}
