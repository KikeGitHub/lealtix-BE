package com.lealtixservice.service.impl;

import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.RedeemCouponRequest;
import com.lealtixservice.dto.RedemptionResponse;
import com.lealtixservice.entity.*;
import com.lealtixservice.enums.RewardType;
import com.lealtixservice.repository.CouponRedemptionRepository;
import com.lealtixservice.repository.CouponRepository;
import com.lealtixservice.service.CouponRedemptionService;
import com.lealtixservice.service.Emailservice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementación del servicio de redención de cupones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRedemptionServiceImpl implements CouponRedemptionService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final Emailservice emailService;

    @Value("${sendgrid.templates.coupon-redemption}")
    private String couponRedemptionTemplateId;

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

        // 4. Calcular descuentos si hay monto original
        BigDecimal originalAmount = request.getOriginalAmount();
        BigDecimal discountAmount = null;
        BigDecimal finalAmount = null;
        RewardType couponType = null;
        BigDecimal couponValue = null;

        PromotionReward reward = campaign.getPromotionReward();
        if (reward != null && originalAmount != null && originalAmount.compareTo(BigDecimal.ZERO) > 0) {
            couponType = reward.getRewardType();

            if (couponType == RewardType.PERCENT_DISCOUNT && reward.getNumericValue() != null) {
                // Descuento porcentual
                couponValue = reward.getNumericValue();
                discountAmount = originalAmount
                        .multiply(couponValue)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                finalAmount = originalAmount.subtract(discountAmount);

            } else if (couponType == RewardType.FIXED_AMOUNT && reward.getNumericValue() != null) {
                // Descuento de monto fijo
                couponValue = reward.getNumericValue();
                discountAmount = couponValue;
                finalAmount = originalAmount.subtract(discountAmount);

                // Validar que el monto final no sea negativo
                if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    finalAmount = BigDecimal.ZERO;
                    discountAmount = originalAmount;
                }
            }

            log.info("Cálculo de descuento - Tipo: {}, Valor: {}, Original: {}, Descuento: {}, Final: {}",
                    couponType, couponValue, originalAmount, discountAmount, finalAmount);
        }

        // 5. Crear registro de auditoría
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
                .originalAmount(originalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .couponType(couponType)
                .couponValue(couponValue)
                .redeemedAt(LocalDateTime.now())
                .build();

        redemption = redemptionRepository.save(redemption);

        // 6. Guardar cambios en el cupón
        couponRepository.save(coupon);

        log.info("Cupón {} redimido exitosamente. Redemption ID: {}", coupon.getCode(), redemption.getId());

        // 7. Enviar email de confirmación de redención
        try {
            sendRedemptionEmail(coupon.getId(), customer, tenant, coupon, redemption.getId(), originalAmount, discountAmount, finalAmount);
            log.info("Email de redención enviado a: {}", customer.getEmail());
        } catch (Exception e) {
            log.error("Error al enviar email de redención para cupón {}: {}", coupon.getCode(), e.getMessage());
            // No fallar la redención si el email falla
        }

        // 8. Construir respuesta
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
                tenant.getNombreNegocio(),
                originalAmount,
                discountAmount,
                finalAmount,
                couponType,
                couponValue
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

    /**
     * Envía email de confirmación de redención al cliente.
     */
    private void sendRedemptionEmail(Long couponId, TenantCustomer customer, Tenant tenant, Coupon coupon,
                                     String redemptionId, BigDecimal originalAmount, BigDecimal discountAmount,
                                     BigDecimal finalAmount) throws IOException {

        // Formatear fecha de redención
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String redemptionDate = LocalDateTime.now().format(dateFormatter);

        // Formatear montos en MXN
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String originalAmountStr = originalAmount != null ? currencyFormat.format(originalAmount) + " MXN" : "N/A";
        String discountAmountStr = discountAmount != null ? "-" + currencyFormat.format(discountAmount) + " MXN" : "N/A";
        String finalAmountStr = finalAmount != null ? currencyFormat.format(finalAmount) + " MXN" : "N/A";

        // Obtener logo del tenant (usar un placeholder si no existe)
        String logoUrl = tenant.getLogoUrl() != null ? tenant.getLogoUrl() :
                        "https://res.cloudinary.com/demo/image/upload/v1700000000/logo-default.png";

        // Preparar datos dinámicos para el template
        Map<String, Object> dynamicData = new HashMap<>();
        dynamicData.put("couponCode", coupon.getCode());
        dynamicData.put("redemptionDate", redemptionDate);
        dynamicData.put("originalAmount", originalAmountStr);
        dynamicData.put("discountAmount", discountAmountStr);
        dynamicData.put("finalAmount", finalAmountStr);
        dynamicData.put("tenantName", tenant.getNombreNegocio());
        dynamicData.put("customerName", customer.getName());
        dynamicData.put("logoUrl", logoUrl);

        // Crear DTO de email
        EmailDTO emailDTO = EmailDTO.builder()
                .to(customer.getEmail())
                .subject("¡Cupón redimido exitosamente!")
                .templateId(couponRedemptionTemplateId)
                .dynamicData(dynamicData)
                .entityType("COUPON_REDEMPTION")
                .entityId(couponId) // Usar couponId como referencia ya que EntityLog.entityId es Long
                .build();

        // Enviar email
        emailService.sendEmailWithTemplate(emailDTO);

        log.info("Email de redención enviado - Cupón: {}, Cliente: {}, Monto final: {}, RedemptionID: {}",
                coupon.getCode(), customer.getEmail(), finalAmountStr, redemptionId);
    }
}

