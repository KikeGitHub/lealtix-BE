package com.lealtixservice.dto.dashboard;

import java.math.BigDecimal;

/**
 * DTO de proyección para el rendimiento de campañas en el dashboard.
 * Contiene métricas agregadas de cupones emitidos, redenciones, ventas y tasas de conversión.
 */
public record CampaignPerformanceDTO(
        Long campaignId,
        String campaignName,
        Long couponsIssued,
        Long redemptions,
        BigDecimal totalSales,
        BigDecimal avgTicket,
        Double redemptionRatePct
) {}

