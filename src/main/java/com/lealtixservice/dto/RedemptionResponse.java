package com.lealtixservice.dto;

import com.lealtixservice.enums.RedemptionChannel;
import com.lealtixservice.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta tras redimir un cupón exitosamente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionResponse {

    private boolean success;
    private String message;

    // Información de la redención
    private String redemptionId;
    private LocalDateTime redeemedAt;
    private String redeemedBy;
    private RedemptionChannel channel;

    // Información del cupón
    private String couponCode;
    private Long couponId;

    // Información de la campaña
    private Long campaignId;
    private String campaignTitle;
    private String benefit;

    // Información del cliente
    private String customerName;
    private String customerEmail;

    // Información del tenant
    private Long tenantId;
    private String tenantName;

    // Información de cálculo de descuentos
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private RewardType couponType;
    private BigDecimal couponValue;

    /**
     * Factory method para redención exitosa
     */
    public static RedemptionResponse success(
            String redemptionId, LocalDateTime redeemedAt, String redeemedBy, RedemptionChannel channel,
            String couponCode, Long couponId,
            Long campaignId, String campaignTitle, String benefit,
            String customerName, String customerEmail,
            Long tenantId, String tenantName,
            BigDecimal originalAmount, BigDecimal discountAmount, BigDecimal finalAmount,
            RewardType couponType, BigDecimal couponValue) {

        return RedemptionResponse.builder()
                .success(true)
                .message("Cupón redimido exitosamente")
                .redemptionId(redemptionId)
                .redeemedAt(redeemedAt)
                .redeemedBy(redeemedBy)
                .channel(channel)
                .couponCode(couponCode)
                .couponId(couponId)
                .campaignId(campaignId)
                .campaignTitle(campaignTitle)
                .benefit(benefit)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .tenantId(tenantId)
                .tenantName(tenantName)
                .originalAmount(originalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .couponType(couponType)
                .couponValue(couponValue)
                .build();
    }

    /**
     * Factory method para redención fallida
     */
    public static RedemptionResponse failure(String message) {
        return RedemptionResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}

