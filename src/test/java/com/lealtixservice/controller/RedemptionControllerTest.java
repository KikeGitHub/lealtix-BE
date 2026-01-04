package com.lealtixservice.controller;

import com.lealtixservice.dto.CouponValidationResponse;
import com.lealtixservice.dto.RedeemCouponRequest;
import com.lealtixservice.dto.RedemptionResponse;
import com.lealtixservice.entity.CouponRedemption;
import com.lealtixservice.enums.CouponStatus;
import com.lealtixservice.enums.RedemptionChannel;
import com.lealtixservice.service.CouponRedemptionService;
import com.lealtixservice.service.CouponValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para RedemptionController
 */
class RedemptionControllerTest {

    @Mock
    private CouponValidationService validationService;

    @Mock
    private CouponRedemptionService redemptionService;

    @InjectMocks
    private RedemptionController controller;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    // ========== Tests para validación con tenantId (Staff) ==========

    @Test
    void validateByQrToken_validCoupon_returnsOk() {
        // Arrange
        String qrToken = "valid-qr-token-123";
        Long tenantId = 1L;

        CouponValidationResponse validResponse = CouponValidationResponse.validCoupon(
                "CUPON123",
                CouponStatus.ACTIVE,
                LocalDateTime.now().plusDays(7),
                "Campaña de Prueba",
                "Descripción de campaña",
                "20% de descuento",
                "Juan Pérez",
                "juan@example.com",
                100L,
                tenantId,
                "Mi Negocio"
        );

        when(validationService.validateCouponByQrToken(qrToken, tenantId)).thenReturn(validResponse);

        // Act
        ResponseEntity<CouponValidationResponse> response = controller.validateByQrToken(qrToken, tenantId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
        assertEquals("CUPON123", response.getBody().getCouponCode());
        verify(validationService, times(1)).validateCouponByQrToken(qrToken, tenantId);
    }

    @Test
    void validateByQrToken_invalidCoupon_returnsBadRequest() {
        // Arrange
        String qrToken = "invalid-qr-token";
        Long tenantId = 1L;

        CouponValidationResponse invalidResponse = CouponValidationResponse.invalidCoupon("Cupón no encontrado");
        when(validationService.validateCouponByQrToken(qrToken, tenantId)).thenReturn(invalidResponse);

        // Act
        ResponseEntity<CouponValidationResponse> response = controller.validateByQrToken(qrToken, tenantId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertEquals("Cupón no encontrado", response.getBody().getMessage());
    }

    @Test
    void validateByQrToken_expiredCoupon_returnsBadRequest() {
        // Arrange
        String qrToken = "expired-qr-token";
        Long tenantId = 1L;

        CouponValidationResponse expiredResponse = CouponValidationResponse.expired(
                "CUPON456",
                LocalDateTime.now().minusDays(1),
                "Campaña Expirada"
        );
        when(validationService.validateCouponByQrToken(qrToken, tenantId)).thenReturn(expiredResponse);

        // Act
        ResponseEntity<CouponValidationResponse> response = controller.validateByQrToken(qrToken, tenantId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertTrue(response.getBody().isExpired());
    }

    // ========== Tests para validación sin tenantId (Cliente) ==========

    @Test
    void validateByQrTokenForCustomer_validCoupon_returnsOk() {
        // Arrange
        String qrToken = "customer-qr-token-789";

        CouponValidationResponse validResponse = CouponValidationResponse.validCoupon(
                "CUPON789",
                CouponStatus.ACTIVE,
                LocalDateTime.now().plusDays(14),
                "Promoción Especial",
                "Obtén 30% de descuento en tu próxima compra",
                "30% de descuento",
                "María González",
                "maria@example.com",
                200L,
                5L,
                "Restaurante XYZ"
        );

        when(validationService.validateCouponByQrTokenForCustomer(qrToken)).thenReturn(validResponse);

        // Act
        ResponseEntity<CouponValidationResponse> response = controller.validateByQrTokenForCustomer(qrToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
        assertEquals("CUPON789", response.getBody().getCouponCode());
        assertEquals("Promoción Especial", response.getBody().getCampaignTitle());
        assertEquals("30% de descuento", response.getBody().getBenefit());
        assertEquals("María González", response.getBody().getCustomerName());
        assertEquals("Restaurante XYZ", response.getBody().getTenantName());
        verify(validationService, times(1)).validateCouponByQrTokenForCustomer(qrToken);
    }

    @Test
    void validateByQrTokenForCustomer_couponNotFound_returnsBadRequest() {
        // Arrange
        String qrToken = "non-existent-token";

        CouponValidationResponse invalidResponse = CouponValidationResponse.invalidCoupon("Cupón no encontrado");
        when(validationService.validateCouponByQrTokenForCustomer(qrToken)).thenReturn(invalidResponse);

        // Act
        ResponseEntity<CouponValidationResponse> response = controller.validateByQrTokenForCustomer(qrToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertEquals("Cupón no encontrado", response.getBody().getMessage());
        verify(validationService, times(1)).validateCouponByQrTokenForCustomer(qrToken);
    }

    @Test
    void validateByQrTokenForCustomer_alreadyRedeemed_returnsBadRequest() {
        // Arrange
        String qrToken = "redeemed-token";

        CouponValidationResponse redeemedResponse = CouponValidationResponse.alreadyRedeemed(
                "CUPON999",
                LocalDateTime.now().minusHours(2),
                "Campaña Test"
        );
        when(validationService.validateCouponByQrTokenForCustomer(qrToken)).thenReturn(redeemedResponse);

        // Act
        ResponseEntity<CouponValidationResponse> response = controller.validateByQrTokenForCustomer(qrToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertTrue(response.getBody().isAlreadyRedeemed());
        assertEquals("Este cupón ya fue redimido", response.getBody().getMessage());
    }

    // ========== Tests para validación por código ==========

    @Test
    void validateByCode_validCoupon_returnsOk() {
        // Arrange
        String code = "PROMO2024";
        Long tenantId = 1L;

        CouponValidationResponse validResponse = CouponValidationResponse.validCoupon(
                code,
                CouponStatus.ACTIVE,
                LocalDateTime.now().plusDays(30),
                "Promoción 2024",
                "Descuento especial",
                "15% OFF",
                "Cliente Test",
                "test@example.com",
                300L,
                tenantId,
                "Negocio ABC"
        );

        when(validationService.validateCouponByCode(code, tenantId)).thenReturn(validResponse);

        // Act
        ResponseEntity<CouponValidationResponse> response = controller.validateByCode(code, tenantId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
    }

    // ========== Tests para redención ==========

    @Test
    void redeemByQrToken_success_returnsOk() {
        // Arrange
        String qrToken = "redeem-token-123";
        Long tenantId = 1L;
        RedeemCouponRequest request = new RedeemCouponRequest();
        request.setRedeemedBy("Staff User");

        RedemptionResponse successResponse = RedemptionResponse.success(
                "RD12345678",                   // redemptionId (String UID de 10 posiciones)
                LocalDateTime.now(),            // redeemedAt
                "Staff User",                   // redeemedBy
                RedemptionChannel.QR_ADMIN,     // channel
                "CUPON123",                     // couponCode
                100L,                           // couponId
                200L,                           // campaignId
                "Campaña de Prueba",            // campaignTitle
                "20% de descuento",             // benefit
                "Juan Pérez",                   // customerName
                "juan@example.com",             // customerEmail
                tenantId,                       // tenantId
                "Mi Negocio",                   // tenantName
                null,                           // originalAmount
                null,                           // discountAmount
                null,                           // finalAmount
                null,                           // couponType
                null                            // couponValue
        );

        when(redemptionService.redeemCouponByQrToken(qrToken, request, tenantId)).thenReturn(successResponse);

        // Act
        ResponseEntity<RedemptionResponse> response = controller.redeemByQrToken(qrToken, tenantId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void redeemByQrToken_alreadyRedeemed_returnsBadRequest() {
        // Arrange
        String qrToken = "already-redeemed-token";
        Long tenantId = 1L;
        RedeemCouponRequest request = new RedeemCouponRequest();

        when(redemptionService.redeemCouponByQrToken(qrToken, request, tenantId))
                .thenThrow(new IllegalArgumentException("Cupón ya redimido"));

        // Act
        ResponseEntity<RedemptionResponse> response = controller.redeemByQrToken(qrToken, tenantId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // ========== Tests para historial ==========

    @Test
    void getRedemptionHistory_returnsListOfRedemptions() {
        // Arrange
        Long tenantId = 1L;
        List<CouponRedemption> redemptions = Arrays.asList(
                new CouponRedemption(),
                new CouponRedemption()
        );

        when(redemptionService.getRedemptionsByTenant(tenantId)).thenReturn(redemptions);

        // Act
        ResponseEntity<List<CouponRedemption>> response = controller.getRedemptionHistory(tenantId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getRedemptionsByCampaign_returnsListOfRedemptions() {
        // Arrange
        Long campaignId = 100L;
        Long tenantId = 1L;
        List<CouponRedemption> redemptions = java.util.Collections.singletonList(new CouponRedemption());

        when(redemptionService.getRedemptionsByCampaign(campaignId, tenantId)).thenReturn(redemptions);

        // Act
        ResponseEntity<List<CouponRedemption>> response = controller.getRedemptionsByCampaign(campaignId, tenantId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getRecentRedemptions_returnsLimitedList() {
        // Arrange
        Long tenantId = 1L;
        int limit = 5;
        List<CouponRedemption> redemptions = Arrays.asList(
                new CouponRedemption(),
                new CouponRedemption(),
                new CouponRedemption()
        );

        when(redemptionService.getRecentRedemptions(tenantId, limit)).thenReturn(redemptions);

        // Act
        ResponseEntity<List<CouponRedemption>> response = controller.getRecentRedemptions(tenantId, limit);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
    }
}

