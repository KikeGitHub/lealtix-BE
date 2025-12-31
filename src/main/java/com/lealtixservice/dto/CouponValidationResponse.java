package com.lealtixservice.dto;

import com.lealtixservice.enums.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para validación de cupón (preview antes de redimir).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponse {

    private boolean valid; // Si el cupón es válido para redimir

    private String message; // Mensaje descriptivo del estado

    // Información del cupón
    private String couponCode;
    private CouponStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime redeemedAt;
    private boolean isExpired;
    private boolean alreadyRedeemed;

    // Información de la campaña
    private Long campaignId;
    private String campaignTitle;
    private String campaignDescription;
    private String benefit; // Descripción del beneficio

    // Información del cliente
    private String customerName;
    private String customerEmail;

    // Información del tenant
    private Long tenantId;
    private String tenantName;

    /**
     * Factory method para cupón válido
     */
    public static CouponValidationResponse validCoupon(
            String code, CouponStatus status, LocalDateTime expiresAt,
            String campaignTitle, String campaignDescription, String benefit,
            String customerName, String customerEmail,
            Long campaignId, Long tenantId, String tenantName) {

        return CouponValidationResponse.builder()
                .valid(true)
                .message("Cupón válido y listo para redimir")
                .couponCode(code)
                .status(status)
                .expiresAt(expiresAt)
                .isExpired(false)
                .alreadyRedeemed(false)
                .campaignId(campaignId)
                .campaignTitle(campaignTitle)
                .campaignDescription(campaignDescription)
                .benefit(benefit)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .tenantId(tenantId)
                .tenantName(tenantName)
                .build();
    }

    /**
     * Factory method para cupón inválido
     */
    public static CouponValidationResponse invalidCoupon(String message) {
        return CouponValidationResponse.builder()
                .valid(false)
                .message(message)
                .build();
    }

    /**
     * Factory method para cupón ya redimido
     */
    public static CouponValidationResponse alreadyRedeemed(
            String code, LocalDateTime redeemedAt, String campaignTitle) {

        return CouponValidationResponse.builder()
                .valid(false)
                .message("Este cupón ya fue redimido")
                .couponCode(code)
                .status(CouponStatus.REDEEMED)
                .redeemedAt(redeemedAt)
                .alreadyRedeemed(true)
                .campaignTitle(campaignTitle)
                .build();
    }

    /**
     * Factory method para cupón expirado
     */
    public static CouponValidationResponse expired(
            String code, LocalDateTime expiresAt, String campaignTitle) {

        return CouponValidationResponse.builder()
                .valid(false)
                .message("Este cupón ha expirado")
                .couponCode(code)
                .status(CouponStatus.EXPIRED)
                .expiresAt(expiresAt)
                .isExpired(true)
                .campaignTitle(campaignTitle)
                .build();
    }
}

