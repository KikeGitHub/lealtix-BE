package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para reportes de cupones por campaña.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponCampaignReport {

    private Long campaignId;
    private String campaignTitle;
    private String campaignDescription;

    private long totalSent;      // Total de cupones enviados
    private long totalRedeemed;  // Total de cupones redimidos
    private long totalActive;    // Total de cupones activos
    private long totalExpired;   // Total de cupones expirados

    private double redemptionRate; // Porcentaje de redención

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

