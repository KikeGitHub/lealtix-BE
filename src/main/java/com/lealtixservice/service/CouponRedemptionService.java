package com.lealtixservice.service;

import com.lealtixservice.dto.RedeemCouponRequest;
import com.lealtixservice.dto.RedemptionResponse;
import com.lealtixservice.entity.CouponRedemption;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar la redención de cupones y su auditoría.
 */
public interface CouponRedemptionService {

    /**
     * Redime un cupón por su QR token.
     * Valida el cupón, registra la redención y actualiza el estado.
     *
     * @param qrToken Token QR del cupón
     * @param request Datos de la redención
     * @param tenantId ID del tenant que redime (seguridad multi-tenant)
     * @return Respuesta con información de la redención
     * @throws IllegalArgumentException si el cupón no es válido
     * @throws IllegalStateException si el cupón ya fue redimido o está expirado
     */
    RedemptionResponse redeemCouponByQrToken(String qrToken, RedeemCouponRequest request, Long tenantId);

    /**
     * Redime un cupón por su código.
     *
     * @param couponCode Código del cupón
     * @param request Datos de la redención
     * @param tenantId ID del tenant que redime
     * @return Respuesta con información de la redención
     */
    RedemptionResponse redeemCouponByCode(String couponCode, RedeemCouponRequest request, Long tenantId);

    /**
     * Obtiene el historial de redenciones de un tenant.
     *
     * @param tenantId ID del tenant
     * @return Lista de redenciones
     */
    List<CouponRedemption> getRedemptionsByTenant(Long tenantId);

    /**
     * Obtiene el historial de redenciones de una campaña.
     *
     * @param campaignId ID de la campaña
     * @param tenantId ID del tenant (para validación)
     * @return Lista de redenciones
     */
    List<CouponRedemption> getRedemptionsByCampaign(Long campaignId, Long tenantId);

    /**
     * Obtiene redenciones en un rango de fechas para un tenant.
     *
     * @param tenantId ID del tenant
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return Lista de redenciones
     */
    List<CouponRedemption> getRedemptionsByDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Obtiene las últimas N redenciones de un tenant.
     *
     * @param tenantId ID del tenant
     * @param limit Cantidad de registros
     * @return Lista de redenciones recientes
     */
    List<CouponRedemption> getRecentRedemptions(Long tenantId, int limit);
}

