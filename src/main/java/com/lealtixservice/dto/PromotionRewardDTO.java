package com.lealtixservice.dto;

import com.lealtixservice.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO para crear o actualizar un PromotionReward
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRewardDTO {

    @NotNull(message = "El tipo de reward es obligatorio")
    private RewardType rewardType;

    // Para PERCENT_DISCOUNT o FIXED_AMOUNT
    private BigDecimal numericValue;

    // Para FREE_PRODUCT
    private Long productId;

    // Para BUY_X_GET_Y
    private Integer buyQuantity;
    private Integer freeQuantity;

    // Para CUSTOM
    private String customConfig;

    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String description;

    private BigDecimal minPurchaseAmount;

    private Integer usageLimit;
}
