package com.lealtixservice.entity;

import com.lealtixservice.enums.CouponStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon", indexes = {
        @Index(name = "idx_coupon_code", columnList = "code", unique = true),
        @Index(name = "idx_coupon_qr_token", columnList = "qr_token", unique = true),
        @Index(name = "idx_coupon_campaign", columnList = "campaign_id"),
        @Index(name = "idx_coupon_customer", columnList = "customer_id"),
        @Index(name = "idx_coupon_status", columnList = "status"),
        @Index(name = "idx_coupon_expires_at", columnList = "expires_at"),
        // Índices compuestos para reportes
        @Index(name = "idx_coupon_campaign_created", columnList = "campaign_id,created_at"),
        @Index(name = "idx_coupon_campaign_status", columnList = "campaign_id,status")
})
@Getter
@Setter
@ToString(exclude = {"campaign", "customer"})
@EqualsAndHashCode(exclude = {"campaign", "customer"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private TenantCustomer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private CouponStatus status = CouponStatus.ACTIVE;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    // Token único para redención QR (no expone IDs internos)
    @Column(name = "qr_token", length = 64, unique = true)
    private String qrToken;

    // URL del código QR para canjear el cupón
    @Column(name = "qr_url", length = 500)
    private String qrUrl;

    // Usuario o sistema que redimió el cupón (email, userId, etc.)
    @Column(name = "redeemed_by", length = 200)
    private String redeemedBy;

    // Metadata adicional (ej. punto de venta donde se redimió)
    @Column(name = "redemption_metadata", columnDefinition = "TEXT")
    private String redemptionMetadata;

    @PrePersist
    protected void onCreate() {
        if (this.code == null || this.code.trim().isEmpty()) {
            this.code = generateCouponCode();
        }
        if (this.qrToken == null || this.qrToken.trim().isEmpty()) {
            this.qrToken = generateQrToken();
        }
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = CouponStatus.ACTIVE;
        }
    }

    /**
     * Genera un código único de cupón basado en UUID.
     * Formato: 12 caracteres alfanuméricos en mayúsculas.
     *
     * @return Código de cupón único
     */
    private String generateCouponCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    /**
     * Genera un token QR único y seguro para redención.
     * Este token NO expone IDs internos y es usado para validación y redención.
     *
     * @return Token QR único (64 caracteres hexadecimales)
     */
    private String generateQrToken() {
        return UUID.randomUUID().toString().replace("-", "") +
               UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Redime el cupón sin metadata adicional.
     * IMPORTANTE: Este método debe ser llamado desde CouponService
     * dentro de una transacción @Transactional.
     *
     * @throws IllegalStateException si el cupón no está ACTIVE o está expirado
     */
    public void redeem() {
        this.redeem(null, null);
    }

    /**
     * Redime el cupón con metadata adicional (ej: punto de venta, usuario).
     * IMPORTANTE: Este método debe ser llamado desde CouponService
     * dentro de una transacción @Transactional.
     *
     * @param redeemedBy Usuario/sistema que redime el cupón
     * @param metadata Información adicional sobre la redención (JSON o texto)
     * @throws IllegalStateException si el cupón no está ACTIVE o está expirado
     */
    public void redeem(String redeemedBy, String metadata) {
        if (this.status != CouponStatus.ACTIVE) {
            throw new IllegalStateException("El cupón no está en estado ACTIVE");
        }
        if (isExpired()) {
            throw new IllegalStateException("El cupón está expirado");
        }
        this.status = CouponStatus.REDEEMED;
        this.redeemedAt = LocalDateTime.now();
        this.redeemedBy = redeemedBy;
        this.redemptionMetadata = metadata;
    }

    /**
     * Verifica si el cupón ha expirado según su fecha de expiración.
     *
     * @return true si expiresAt es anterior a la fecha/hora actual, false en caso contrario
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Cancela el cupón.
     * Transición de estado: cualquier estado -> CANCELLED
     */
    public void cancel() {
        this.status = CouponStatus.CANCELLED;
    }

    /**
     * Marca el cupón como expirado si está activo.
     * Transición de estado: ACTIVE -> EXPIRED
     * Útil para procesos batch que expiran cupones automáticamente.
     */
    public void markAsExpired() {
        if (this.status == CouponStatus.ACTIVE) {
            this.status = CouponStatus.EXPIRED;
        }
    }

    /**
     * Valida si el cupón puede ser redimido en este momento.
     *
     * @return true si está ACTIVE y no ha expirado, false en caso contrario
     */
    public boolean canBeRedeemed() {
        return this.status == CouponStatus.ACTIVE && !isExpired();
    }
}

