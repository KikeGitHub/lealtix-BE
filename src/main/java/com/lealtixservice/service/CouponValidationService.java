package com.lealtixservice.service;

import com.lealtixservice.dto.CouponValidationResponse;

/**
 * Servicio para validar cupones antes de redimir.
 * Separa la lógica de validación de la redención.
 */
public interface CouponValidationService {

    /**
     * Valida un cupón por su QR token sin redimirlo.
     * Proporciona información sobre el estado del cupón y si puede ser redimido.
     *
     * @param qrToken Token QR del cupón
     * @param tenantId ID del tenant que intenta validar (seguridad multi-tenant)
     * @return Información de validación del cupón
     */
    CouponValidationResponse validateCouponByQrToken(String qrToken, Long tenantId);

    /**
     * Valida un cupón por su código sin redimirlo.
     *
     * @param couponCode Código del cupón
     * @param tenantId ID del tenant que intenta validar
     * @return Información de validación del cupón
     */
    CouponValidationResponse validateCouponByCode(String couponCode, Long tenantId);

    /**
     * Valida un cupón por su QR token desde la perspectiva del cliente.
     * Este método NO requiere tenantId, es para que el cliente vea los detalles de su cupón.
     *
     * @param qrToken Token QR del cupón
     * @return Información completa del cupón para mostrar al cliente
     */
    CouponValidationResponse validateCouponByQrTokenForCustomer(String qrToken);
}

