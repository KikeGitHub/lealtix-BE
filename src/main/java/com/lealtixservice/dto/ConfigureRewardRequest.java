package com.lealtixservice.dto;

import com.lealtixservice.enums.RewardType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para configurar o actualizar el reward de una campaña.
 * Usado en el endpoint POST /campaigns/{campaignId}/reward
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigureRewardRequest {

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

    // Campos opcionales
    private String description;
    private BigDecimal minPurchaseAmount;
    private Integer usageLimit;
}

