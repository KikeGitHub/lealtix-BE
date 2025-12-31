package com.lealtixservice.repository;

import com.lealtixservice.entity.CouponRedemption;
import com.lealtixservice.enums.RedemptionChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, String> {

    /**
     * Buscar redenciones por tenant
     */
    List<CouponRedemption> findByTenantId(Long tenantId);

    /**
     * Buscar redenciones por campaña
     */
    List<CouponRedemption> findByCampaignId(Long campaignId);

    /**
     * Buscar redenciones por tenant y campaña
     */
    List<CouponRedemption> findByTenantIdAndCampaignId(Long tenantId, Long campaignId);

    /**
     * Buscar redención por cupón (debe ser única)
     */
    Optional<CouponRedemption> findByCouponId(Long couponId);

    /**
     * Verificar si un cupón ya fue redimido
     */
    boolean existsByCouponId(Long couponId);

    /**
     * Contar redenciones por tenant
     */
    long countByTenantId(Long tenantId);

    /**
     * Contar redenciones por campaña
     */
    long countByCampaignId(Long campaignId);

    /**
     * Contar redenciones por tenant y canal
     */
    long countByTenantIdAndChannel(Long tenantId, RedemptionChannel channel);

    /**
     * Redenciones en un rango de fechas para un tenant
     */
    @Query("SELECT r FROM CouponRedemption r WHERE r.tenantId = :tenantId " +
           "AND r.redeemedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY r.redeemedAt DESC")
    List<CouponRedemption> findByTenantIdAndDateRange(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Redenciones en un rango de fechas para una campaña
     */
    @Query("SELECT r FROM CouponRedemption r WHERE r.campaignId = :campaignId " +
           "AND r.redeemedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY r.redeemedAt DESC")
    List<CouponRedemption> findByCampaignIdAndDateRange(
            @Param("campaignId") Long campaignId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Redenciones recientes por tenant (últimas N)
     */
    @Query("SELECT r FROM CouponRedemption r WHERE r.tenantId = :tenantId " +
           "ORDER BY r.redeemedAt DESC LIMIT :limit")
    List<CouponRedemption> findRecentByTenantId(
            @Param("tenantId") Long tenantId,
            @Param("limit") int limit
    );
}

