package com.lealtixservice.service;

import com.lealtixservice.dto.PromotionRewardDTO;
import com.lealtixservice.dto.PromotionRewardResponse;

import java.util.List;

/**
 * Servicio para gestionar PromotionRewards de forma independiente.
 * Operaciones CRUD directas sobre rewards.
 */
public interface PromotionRewardService {

    /**
     * Obtener reward por ID
     */
    PromotionRewardResponse findById(Long rewardId);

    /**
     * Obtener reward por ID de campaña
     */
    PromotionRewardResponse findByCampaignId(Long campaignId);

    /**
     * Listar todos los rewards (opcional, para admin)
     */
    List<PromotionRewardResponse> findAll();

    /**
     * Actualizar un reward existente
     */
    PromotionRewardResponse update(Long rewardId, PromotionRewardDTO dto);

    /**
     * Eliminar un reward (desvincularlo de la campaña)
     */
    void delete(Long rewardId);

    /**
     * Incrementar uso del reward (para aplicar descuentos)
     */
    void incrementUsage(Long rewardId);

    /**
     * Verificar si el reward alcanzó su límite de uso
     */
    boolean isUsageLimitReached(Long rewardId);
}

