package com.lealtixservice.service.impl;

import com.lealtixservice.dto.PromotionRewardDTO;
import com.lealtixservice.dto.PromotionRewardResponse;
import com.lealtixservice.entity.PromotionReward;
import com.lealtixservice.enums.RewardType;
import com.lealtixservice.exception.BusinessRuleException;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.repository.PromotionRewardRepository;
import com.lealtixservice.service.PromotionRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionRewardServiceImpl implements PromotionRewardService {

    private final PromotionRewardRepository promotionRewardRepository;

    private static final int DESCRIPTION_MAX_LENGTH = 500;

    @Override
    @Transactional(readOnly = true)
    public PromotionRewardResponse findById(Long rewardId) {
        log.info("Buscando PromotionReward con ID: {}", rewardId);
        PromotionReward reward = promotionRewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el reward con ID: " + rewardId));
        return mapToResponse(reward);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionRewardResponse findByCampaignId(Long campaignId) {
        log.info("Buscando PromotionReward para campaña ID: {}", campaignId);
        PromotionReward reward = promotionRewardRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró reward para la campaña ID: " + campaignId));
        return mapToResponse(reward);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionRewardResponse> findAll() {
        log.info("Listando todos los PromotionRewards");
        return promotionRewardRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromotionRewardResponse update(Long rewardId, PromotionRewardDTO dto) {
        log.info("Actualizando PromotionReward ID: {} con tipo: {}", rewardId, dto.getRewardType());

        // 1. Buscar reward existente
        PromotionReward reward = promotionRewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el reward con ID: " + rewardId));

        // 2. Validar parámetros según tipo
        validateRewardParameters(dto);

        // 3. Actualizar campos
        reward.setRewardType(dto.getRewardType());
        reward.setNumericValue(dto.getNumericValue());
        reward.setProductId(dto.getProductId());
        reward.setBuyQuantity(dto.getBuyQuantity());
        reward.setFreeQuantity(dto.getFreeQuantity());
        reward.setCustomConfig(dto.getCustomConfig());
        // Sanitizar y validación de longitud para description
        String desc = dto.getDescription() == null ? null : dto.getDescription().trim();
        if (desc != null && desc.length() > DESCRIPTION_MAX_LENGTH) {
            throw new BusinessRuleException("La descripción no puede superar " + DESCRIPTION_MAX_LENGTH + " caracteres");
        }
        reward.setDescription(desc);
        reward.setMinPurchaseAmount(dto.getMinPurchaseAmount());
        reward.setUsageLimit(dto.getUsageLimit());

        // 4. Guardar
        PromotionReward updated = promotionRewardRepository.save(reward);
        log.info("PromotionReward {} actualizado exitosamente", rewardId);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long rewardId) {
        log.info("Eliminando PromotionReward ID: {}", rewardId);
        if (!promotionRewardRepository.existsById(rewardId)) {
            throw new ResourceNotFoundException("No se encontró el reward con ID: " + rewardId);
        }
        promotionRewardRepository.deleteById(rewardId);
        log.info("PromotionReward {} eliminado exitosamente", rewardId);
    }

    @Override
    @Transactional
    public void incrementUsage(Long rewardId) {
        log.info("Incrementando uso del PromotionReward ID: {}", rewardId);
        PromotionReward reward = promotionRewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el reward con ID: " + rewardId));

        if (reward.isUsageLimitReached()) {
            throw new BusinessRuleException(
                    "El reward ha alcanzado su límite de uso (" + reward.getUsageLimit() + ")");
        }

        reward.incrementUsage();
        promotionRewardRepository.save(reward);
        log.info("Uso incrementado para reward {}: {}/{}",
                rewardId, reward.getUsageCount(), reward.getUsageLimit());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsageLimitReached(Long rewardId) {
        return promotionRewardRepository.isUsageLimitReached(rewardId);
    }

    // ===== MÉTODOS AUXILIARES =====

    /**
     * Valida los parámetros del reward según su tipo.
     */
    private void validateRewardParameters(PromotionRewardDTO dto) {
        RewardType type = dto.getRewardType();

        switch (type) {
            case PERCENT_DISCOUNT:
                if (dto.getNumericValue() == null || dto.getNumericValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessRuleException(
                            "Para reward tipo PERCENT_DISCOUNT, el campo 'numericValue' es obligatorio y debe ser mayor a 0");
                }
                if (dto.getNumericValue().compareTo(new BigDecimal("100")) > 0) {
                    throw new BusinessRuleException(
                            "El porcentaje de descuento no puede ser mayor a 100");
                }
                break;

            case FIXED_AMOUNT:
                if (dto.getNumericValue() == null || dto.getNumericValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessRuleException(
                            "Para reward tipo FIXED_AMOUNT, el campo 'numericValue' es obligatorio y debe ser mayor a 0");
                }
                break;

            case FREE_PRODUCT:
                if (dto.getProductId() == null) {
                    throw new BusinessRuleException(
                            "Para reward tipo FREE_PRODUCT, el campo 'productId' es obligatorio");
                }
                break;

            case BUY_X_GET_Y:
                if (dto.getBuyQuantity() == null || dto.getBuyQuantity() <= 0) {
                    throw new BusinessRuleException(
                            "Para reward tipo BUY_X_GET_Y, el campo 'buyQuantity' es obligatorio y debe ser mayor a 0");
                }
                if (dto.getFreeQuantity() == null || dto.getFreeQuantity() <= 0) {
                    throw new BusinessRuleException(
                            "Para reward tipo BUY_X_GET_Y, el campo 'freeQuantity' es obligatorio y debe ser mayor a 0");
                }
                break;

            case CUSTOM:
                // Para CUSTOM no hay validaciones estrictas
                log.info("Reward tipo CUSTOM configurado con customConfig: {}", dto.getCustomConfig());
                break;

            default:
                throw new BusinessRuleException("Tipo de reward no soportado: " + type);
        }
    }

    /**
     * Mapea PromotionReward entity a DTO de respuesta.
     */
    private PromotionRewardResponse mapToResponse(PromotionReward reward) {
        return PromotionRewardResponse.builder()
                .id(reward.getId())
                .campaignId(reward.getCampaign().getId())
                .rewardType(reward.getRewardType())
                .numericValue(reward.getNumericValue())
                .productId(reward.getProductId())
                .buyQuantity(reward.getBuyQuantity())
                .freeQuantity(reward.getFreeQuantity())
                .customConfig(reward.getCustomConfig())
                .description(reward.getDescription())
                .minPurchaseAmount(reward.getMinPurchaseAmount())
                .usageLimit(reward.getUsageLimit())
                .usageCount(reward.getUsageCount())
                .createdAt(reward.getCreatedAt())
                .updatedAt(reward.getUpdatedAt())
                .build();
    }
}

