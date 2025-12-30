package com.lealtixservice.dto;

import com.lealtixservice.enums.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Coupon
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {

    private Long id;
    private String code;
    private Long campaignId;
    private String campaignTitle;
    private Long customerId;
    private String customerName;
    private CouponStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime redeemedAt;
    private String qrCodeUrl; // URL del QR generado

    // Información del reward (denormalizado para facilitar UI)
    private String rewardDescription;
    private String rewardType;
}

