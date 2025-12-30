package com.lealtixservice.dto;

import com.lealtixservice.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta después de configurar un reward.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRewardResponse {

    private Long id;
    private Long campaignId;
    private RewardType rewardType;
    private BigDecimal numericValue;
    private Long productId;
    private Integer buyQuantity;
    private Integer freeQuantity;
    private String customConfig;
    private String description;
    private BigDecimal minPurchaseAmount;
    private Integer usageLimit;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

