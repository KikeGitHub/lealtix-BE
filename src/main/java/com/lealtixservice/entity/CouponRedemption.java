package com.lealtixservice.entity;

import com.lealtixservice.enums.RedemptionChannel;
import com.lealtixservice.enums.RewardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Entidad de auditoría para cada redención de cupón.
 * Proporciona trazabilidad completa de quién, cuándo y cómo se redimió un cupón.
 */
@Entity
@Table(name = "coupon_redemption", indexes = {
        @Index(name = "idx_redemption_coupon", columnList = "coupon_id"),
        @Index(name = "idx_redemption_tenant", columnList = "tenant_id"),
        @Index(name = "idx_redemption_campaign", columnList = "campaign_id"),
        @Index(name = "idx_redemption_date", columnList = "redeemed_at"),
        @Index(name = "idx_redemption_channel", columnList = "channel"),
        // Índices compuestos para reportes y dashboards
        @Index(name = "idx_redemption_tenant_date", columnList = "tenant_id,redeemed_at"),
        @Index(name = "idx_redemption_campaign_date", columnList = "campaign_id,redeemed_at")
})
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRedemption {

    @Id
    @Column(name = "id", length = 10, nullable = false, updatable = false)
    private String id;

    @NotNull
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @NotNull
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @NotNull
    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(name = "customer_email", length = 200)
    private String customerEmail;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @NotBlank
    @Column(name = "redeemed_by", nullable = false, length = 200)
    private String redeemedBy; // Usuario/sistema que ejecutó la redención

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 50)
    private RedemptionChannel channel; // QR, MANUAL, API

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // IP desde donde se redimió

    @Column(name = "user_agent", length = 500)
    private String userAgent; // Info del navegador/app

    @Column(name = "location", length = 200)
    private String location; // Punto de venta, ubicación

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // Información adicional en JSON

    // Campos de cálculo de descuentos
    @Column(name = "original_amount", precision = 10, scale = 2)
    private BigDecimal originalAmount; // Monto original de la cuenta

    // Alias semántico para reportes - apunta al monto total de la compra
    // Usar en queries de dashboard para mayor claridad semántica
    @Column(name = "purchase_amount", precision = 10, scale = 2)
    private BigDecimal purchaseAmount; // Monto de la compra asociada a la redención

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount; // Monto del descuento aplicado

    @Column(name = "final_amount", precision = 10, scale = 2)
    private BigDecimal finalAmount; // Monto final después del descuento

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", length = 50)
    private RewardType couponType; // Tipo de cupón (PERCENT_DISCOUNT, FIXED_AMOUNT, etc.)

    @Column(name = "coupon_value", precision = 10, scale = 2)
    private BigDecimal couponValue; // Valor del cupón (porcentaje o monto fijo)

    @NotNull
    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = generateUid();
        }
        if (this.redeemedAt == null) {
            this.redeemedAt = LocalDateTime.now();
        }
    }

    /**
     * Genera un UID único de 10 posiciones alfanumérico.
     * Formato: Letras mayúsculas y números (0-9, A-Z excluyendo vocales para evitar palabras)
     */
    private String generateUid() {
        String chars = "0123456789BCDFGHJKLMNPQRSTVWXYZ"; // Sin vocales para evitar palabras ofensivas
        Random random = new Random();
        StringBuilder uid = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            uid.append(chars.charAt(random.nextInt(chars.length())));
        }

        return uid.toString();
    }
}

