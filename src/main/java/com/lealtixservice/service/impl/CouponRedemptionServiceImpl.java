package com.lealtixservice.service.impl;

import com.lealtixservice.dto.RedeemCouponRequest;
import com.lealtixservice.dto.RedemptionResponse;
import com.lealtixservice.entity.*;
import com.lealtixservice.repository.CouponRedemptionRepository;
import com.lealtixservice.repository.CouponRepository;
import com.lealtixservice.service.CouponRedemptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de redención de cupones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRedemptionServiceImpl implements CouponRedemptionService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;

    @Override
    @Transactional
    public RedemptionResponse redeemCouponByQrToken(String qrToken, RedeemCouponRequest request, Long tenantId) {
        log.info("Iniciando redención de cupón por QR token para tenant: {}", tenantId);

        // 1. Buscar cupón por QR token
        Coupon coupon = couponRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new IllegalArgumentException("Cupón no encontrado"));

        return redeemCoupon(coupon, request, tenantId);
    }

    @Override
    @Transactional
    public RedemptionResponse redeemCouponByCode(String couponCode, RedeemCouponRequest request, Long tenantId) {
        log.info("Iniciando redención de cupón por código para tenant: {}", tenantId);

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Cupón no encontrado"));

        return redeemCoupon(coupon, request, tenantId);
    }

    /**
     * Lógica común de redención de cupón.
     */
    private RedemptionResponse redeemCoupon(Coupon coupon, RedeemCouponRequest request, Long tenantId) {
        Campaign campaign = coupon.getCampaign();
        TenantCustomer customer = coupon.getCustomer();
        Tenant tenant = customer.getTenant();

        // 1. Validar que el cupón pertenece al tenant
        if (!campaign.getBusinessId().equals(tenantId)) {
            log.warn("Intento de redimir cupón de otro tenant. Cupón tenant: {}, Request tenant: {}",
                    campaign.getBusinessId(), tenantId);
            return RedemptionResponse.failure("Cupón no válido para este negocio");
        }

        // 2. Validar que no fue redimido previamente
        if (redemptionRepository.existsByCouponId(coupon.getId())) {
            log.warn("Intento de redimir cupón ya redimido: {}", coupon.getCode());
            return RedemptionResponse.failure("Este cupón ya fue redimido");
        }

        // 3. Validar estado del cupón y redimirlo
        try {
            coupon.redeem(request.getRedeemedBy(), request.getMetadata());
        } catch (IllegalStateException e) {
            log.warn("Error al redimir cupón {}: {}", coupon.getCode(), e.getMessage());
            return RedemptionResponse.failure(e.getMessage());
        }

        // 4. Crear registro de auditoría
        CouponRedemption redemption = CouponRedemption.builder()
                .couponId(coupon.getId())
                .tenantId(tenantId)
                .campaignId(campaign.getId())
                .customerEmail(customer.getEmail())
                .customerName(customer.getName())
                .redeemedBy(request.getRedeemedBy())
                .channel(request.getChannel())
                .ipAddress(request.getIpAddress())
                .userAgent(request.getUserAgent())
                .location(request.getLocation())
                .metadata(request.getMetadata())
                .redeemedAt(LocalDateTime.now())
                .build();

        redemption = redemptionRepository.save(redemption);

        // 5. Guardar cambios en el cupón
        couponRepository.save(coupon);

        log.info("Cupón {} redimido exitosamente. Redemption ID: {}", coupon.getCode(), redemption.getId());

        // 6. Construir respuesta
        String benefit = getBenefitDescription(campaign);

        return RedemptionResponse.success(
                redemption.getId(),
                redemption.getRedeemedAt(),
                redemption.getRedeemedBy(),
                redemption.getChannel(),
                coupon.getCode(),
                coupon.getId(),
                campaign.getId(),
                campaign.getTitle(),
                benefit,
                customer.getName(),
                customer.getEmail(),
                tenant.getId(),
                tenant.getNombreNegocio()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponRedemption> getRedemptionsByTenant(Long tenantId) {
        log.debug("Obteniendo redenciones para tenant: {}", tenantId);
        return redemptionRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponRedemption> getRedemptionsByCampaign(Long campaignId, Long tenantId) {
        log.debug("Obteniendo redenciones para campaña: {} del tenant: {}", campaignId, tenantId);

        // Validar que la campaña pertenece al tenant
        List<CouponRedemption> redemptions = redemptionRepository.findByCampaignId(campaignId);

        // Filtrar por tenant para seguridad
        return redemptions.stream()
                .filter(r -> r.getTenantId().equals(tenantId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponRedemption> getRedemptionsByDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Obteniendo redenciones para tenant: {} entre {} y {}", tenantId, startDate, endDate);
        return redemptionRepository.findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponRedemption> getRecentRedemptions(Long tenantId, int limit) {
        log.debug("Obteniendo últimas {} redenciones para tenant: {}", limit, tenantId);
        return redemptionRepository.findRecentByTenantId(tenantId, limit);
    }

    /**
     * Obtiene la descripción del beneficio desde PromotionReward si existe.
     */
    private String getBenefitDescription(Campaign campaign) {
        PromotionReward reward = campaign.getPromotionReward();
        if (reward != null && reward.getDescription() != null) {
            return reward.getDescription();
        }
        return campaign.getDescription();
    }
}

