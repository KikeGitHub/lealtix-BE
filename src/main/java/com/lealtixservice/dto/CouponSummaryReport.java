package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para reportes de cupones: resumen general.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponSummaryReport {

    private Long tenantId;
    private String tenantName;

    private long totalCreated;   // Total de cupones creados
    private long totalSent;      // Total de cupones enviados
    private long totalRedeemed;  // Total de cupones redimidos
    private long totalExpired;   // Total de cupones expirados
    private long totalCancelled; // Total de cupones cancelados
    private long totalActive;    // Total de cupones activos

    private double redemptionRate; // Porcentaje de redención (redimidos / enviados)

    /**
     * Calcula el porcentaje de redención
     */
    public void calculateRedemptionRate() {
        if (totalSent > 0) {
            this.redemptionRate = ((double) totalRedeemed / totalSent) * 100.0;
        } else {
            this.redemptionRate = 0.0;
        }
    }
}

