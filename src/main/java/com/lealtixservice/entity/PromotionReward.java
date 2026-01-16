package com.lealtixservice.entity;

import com.lealtixservice.enums.RewardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_reward", indexes = {
        @Index(name = "idx_promotion_reward_campaign", columnList = "campaign_id"),
        @Index(name = "idx_promotion_reward_type", columnList = "reward_type")
})
@Getter
@Setter
@ToString(exclude = {"campaign"})
@EqualsAndHashCode(exclude = {"campaign"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false, unique = true)
    private Campaign campaign;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false, length = 50)
    private RewardType rewardType;

    // Campo numérico genérico para porcentajes o montos fijos
    @Column(name = "numeric_value", precision = 10, scale = 2)
    private BigDecimal numericValue;

    // ID del producto gratis (si aplica)
    @Column(name = "product_id")
    private Long productId;

    // Cantidad que debe comprar (para BUY_X_GET_Y)
    @Column(name = "buy_quantity")
    private Integer buyQuantity;

    // Cantidad gratis (para BUY_X_GET_Y)
    @Column(name = "free_quantity")
    private Integer freeQuantity;

    // Campo flexible para configuraciones custom (JSON string)
    @Column(name = "custom_config", columnDefinition = "TEXT")
    private String customConfig;

    // Descripción del reward para mostrar al usuario
    @Column(name = "description", length = 500, nullable = true)
    private String description;

    // Monto mínimo de compra requerido (opcional)
    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    // Límite de usos del reward (null = ilimitado)
    @Column(name = "usage_limit")
    private Integer usageLimit;

    // Contador de usos actuales
    @Builder.Default
    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Incrementa el contador de uso del reward.
     * IMPORTANTE: Este método debe ser llamado SOLO desde PromotionRewardService
     * dentro de una transacción @Transactional para evitar condiciones de carrera.
     */
    public void incrementUsage() {
        this.usageCount = (this.usageCount == null ? 0 : this.usageCount) + 1;
    }

    /**
     * Verifica si el reward ha alcanzado su límite de uso.
     * @return true si se alcanzó el límite, false si aún puede usarse o no tiene límite
     */
    public boolean isUsageLimitReached() {
        if (usageLimit == null) {
            return false; // Sin límite
        }
        return usageCount >= usageLimit;
    }
}
