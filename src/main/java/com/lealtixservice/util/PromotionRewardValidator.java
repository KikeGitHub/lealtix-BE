package com.lealtixservice.util;

import com.lealtixservice.entity.PromotionReward;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Validador para la configuración de PromotionReward.
 * Centraliza toda la lógica de validación de negocio para rewards.
 */
@Component
public class PromotionRewardValidator {

    /**
     * Valida que la configuración del reward sea correcta según su tipo.
     *
     * @param reward El reward a validar
     * @return true si la configuración es válida, false en caso contrario
     */
    public boolean isValidConfiguration(PromotionReward reward) {
        if (reward == null || reward.getRewardType() == null) {
            return false;
        }

        return switch (reward.getRewardType()) {
            case PERCENT_DISCOUNT -> isValidPercentDiscount(reward);
            case FIXED_AMOUNT -> isValidFixedAmount(reward);
            case FREE_PRODUCT -> isValidFreeProduct(reward);
            case BUY_X_GET_Y -> isValidBuyXGetY(reward);
            case CUSTOM -> isValidCustom(reward);
        };
    }

    /**
     * Valida configuración de descuento porcentual.
     * Reglas: numericValue debe estar entre 0 y 100.
     */
    private boolean isValidPercentDiscount(PromotionReward reward) {
        BigDecimal value = reward.getNumericValue();
        return value != null
                && value.compareTo(BigDecimal.ZERO) > 0
                && value.compareTo(new BigDecimal("100")) <= 0;
    }

    /**
     * Valida configuración de descuento de monto fijo.
     * Reglas: numericValue debe ser mayor a 0.
     */
    private boolean isValidFixedAmount(PromotionReward reward) {
        BigDecimal value = reward.getNumericValue();
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Valida configuración de producto gratis.
     * Reglas: productId debe estar presente.
     */
    private boolean isValidFreeProduct(PromotionReward reward) {
        return reward.getProductId() != null;
    }

    /**
     * Valida configuración de compra X lleva Y.
     * Reglas: buyQuantity y freeQuantity deben ser mayores a 0.
     */
    private boolean isValidBuyXGetY(PromotionReward reward) {
        Integer buyQty = reward.getBuyQuantity();
        Integer freeQty = reward.getFreeQuantity();
        return buyQty != null && buyQty > 0
                && freeQty != null && freeQty > 0;
    }

    /**
     * Valida configuración custom.
     * Reglas: customConfig debe tener contenido.
     */
    private boolean isValidCustom(PromotionReward reward) {
        String config = reward.getCustomConfig();
        return config != null && !config.trim().isEmpty();
    }

    /**
     * Valida y retorna un mensaje de error específico si la configuración es inválida.
     *
     * @param reward El reward a validar
     * @return Mensaje de error, o null si es válido
     */
    public String getValidationError(PromotionReward reward) {
        if (reward == null) {
            return "El reward no puede ser null";
        }

        if (reward.getRewardType() == null) {
            return "El tipo de reward es obligatorio";
        }

        return switch (reward.getRewardType()) {
            case PERCENT_DISCOUNT -> {
                if (reward.getNumericValue() == null) {
                    yield "El descuento porcentual requiere un valor numérico";
                }
                if (reward.getNumericValue().compareTo(BigDecimal.ZERO) <= 0) {
                    yield "El descuento porcentual debe ser mayor a 0";
                }
                if (reward.getNumericValue().compareTo(new BigDecimal("100")) > 0) {
                    yield "El descuento porcentual no puede ser mayor a 100";
                }
                yield null;
            }
            case FIXED_AMOUNT -> {
                if (reward.getNumericValue() == null) {
                    yield "El descuento fijo requiere un valor numérico";
                }
                if (reward.getNumericValue().compareTo(BigDecimal.ZERO) <= 0) {
                    yield "El descuento fijo debe ser mayor a 0";
                }
                yield null;
            }
            case FREE_PRODUCT -> {
                if (reward.getProductId() == null) {
                    yield "El producto gratis requiere un ID de producto";
                }
                yield null;
            }
            case BUY_X_GET_Y -> {
                if (reward.getBuyQuantity() == null || reward.getBuyQuantity() <= 0) {
                    yield "La cantidad de compra debe ser mayor a 0";
                }
                if (reward.getFreeQuantity() == null || reward.getFreeQuantity() <= 0) {
                    yield "La cantidad gratis debe ser mayor a 0";
                }
                yield null;
            }
            case CUSTOM -> {
                if (reward.getCustomConfig() == null || reward.getCustomConfig().trim().isEmpty()) {
                    yield "El reward custom requiere una configuración";
                }
                yield null;
            }
        };
    }
}

