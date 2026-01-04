package com.lealtixservice.dto.dashboard;


/**
 * DTO de proyección para estadísticas de cupones por campaña.
 * Contiene totales de cupones creados vs redimidos y tasas de redención.
 */
public record CouponStatsDTO(
        Long campaignId,
        String campaignName,
        Long couponsCreated,
        Long couponsRedeemed,
        Double redemptionRatePct
) {}

