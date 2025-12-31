package com.lealtixservice.controller;

import com.lealtixservice.dto.CouponValidationResponse;
import com.lealtixservice.dto.RedeemCouponRequest;
import com.lealtixservice.dto.RedemptionResponse;
import com.lealtixservice.entity.CouponRedemption;
import com.lealtixservice.service.CouponRedemptionService;
import com.lealtixservice.service.CouponValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para redención de cupones.
 * Endpoints públicos (con validación de tenant) para redimir cupones vía QR o código.
 */
@Slf4j
@RestController
@RequestMapping("/api/redemptions")
@Tag(name = "Redemption", description = "Operaciones de validación y redención de cupones")
@RequiredArgsConstructor
public class RedemptionController {

    private final CouponValidationService validationService;
    private final CouponRedemptionService redemptionService;

    /**
     * VALIDAR cupón por QR token (preview, NO redime).
     * Endpoint público para que clientes validen sus cupones antes de redimir.
     */
    @Operation(summary = "Validar cupón por QR token (preview, no redime)",
            description = "Verifica el estado de un cupón sin redimirlo. Útil para mostrar información al cliente antes de confirmar.")
    @GetMapping("/validate/qr/{qrToken}")
    public ResponseEntity<CouponValidationResponse> validateByQrToken(
            @PathVariable String qrToken,
            @RequestParam Long tenantId) {

        log.info("Validando cupón por QR token para tenant: {}", tenantId);

        CouponValidationResponse response = validationService.validateCouponByQrToken(qrToken, tenantId);

        if (response.isValid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * VALIDAR cupón por QR token desde perspectiva del cliente (NO requiere tenantId).
     * Endpoint público para que clientes vean los detalles de su cupón.
     */
    @Operation(summary = "Validar cupón por QR token para cliente (sin tenantId)",
            description = "Verifica el estado de un cupón desde la perspectiva del cliente. Muestra todos los detalles del cupón sin requerir tenantId.")
    @GetMapping("/validate/customer/qr/{qrToken}")
    public ResponseEntity<CouponValidationResponse> validateByQrTokenForCustomer(
            @PathVariable String qrToken) {

        log.info("Validando cupón por QR token desde perspectiva del cliente");

        CouponValidationResponse response = validationService.validateCouponByQrTokenForCustomer(qrToken);

        if (response.isValid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * VALIDAR cupón por código (preview, NO redime).
     */
    @Operation(summary = "Validar cupón por código (preview, no redime)",
            description = "Verifica el estado de un cupón por su código sin redimirlo.")
    @GetMapping("/validate/code/{code}")
    public ResponseEntity<CouponValidationResponse> validateByCode(
            @PathVariable String code,
            @RequestParam Long tenantId) {

        log.info("Validando cupón por código para tenant: {}", tenantId);

        CouponValidationResponse response = validationService.validateCouponByCode(code, tenantId);

        if (response.isValid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * REDIMIR cupón por QR token (acción definitiva).
     * Este endpoint ejecuta la redención y no puede deshacerse.
     */
    @Operation(summary = "Redimir cupón por QR token",
            description = "Redime un cupón de forma definitiva. Solo puede redimirse una vez.")
    @PostMapping("/redeem/qr/{qrToken}")
    public ResponseEntity<RedemptionResponse> redeemByQrToken(
            @PathVariable String qrToken,
            @RequestParam Long tenantId,
            @Valid @RequestBody RedeemCouponRequest request) {

        log.info("Redimiendo cupón por QR token para tenant: {}", tenantId);

        try {
            RedemptionResponse response = redemptionService.redeemCouponByQrToken(qrToken, request, tenantId);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IllegalArgumentException e) {
            log.error("Error al redimir cupón: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RedemptionResponse.failure(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al redimir cupón", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RedemptionResponse.failure("Error interno del servidor"));
        }
    }

    /**
     * REDIMIR cupón por código (acción definitiva).
     */
    @Operation(summary = "Redimir cupón por código",
            description = "Redime un cupón por su código de forma definitiva.")
    @PostMapping("/redeem/code/{code}")
    public ResponseEntity<RedemptionResponse> redeemByCode(
            @PathVariable String code,
            @RequestParam Long tenantId,
            @Valid @RequestBody RedeemCouponRequest request) {

        log.info("Redimiendo cupón por código para tenant: {}", tenantId);

        try {
            RedemptionResponse response = redemptionService.redeemCouponByCode(code, request, tenantId);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IllegalArgumentException e) {
            log.error("Error al redimir cupón: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RedemptionResponse.failure(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al redimir cupón", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RedemptionResponse.failure("Error interno del servidor"));
        }
    }

    /**
     * Obtener historial de redenciones por tenant.
     */
    @Operation(summary = "Obtener historial de redenciones del tenant",
            description = "Lista todas las redenciones realizadas por un tenant.")
    @GetMapping("/history")
    public ResponseEntity<List<CouponRedemption>> getRedemptionHistory(
            @RequestParam Long tenantId) {

        log.info("Obteniendo historial de redenciones para tenant: {}", tenantId);

        List<CouponRedemption> redemptions = redemptionService.getRedemptionsByTenant(tenantId);
        return ResponseEntity.ok(redemptions);
    }

    /**
     * Obtener redenciones de una campaña específica.
     */
    @Operation(summary = "Obtener redenciones por campaña",
            description = "Lista todas las redenciones de una campaña específica.")
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<CouponRedemption>> getRedemptionsByCampaign(
            @PathVariable Long campaignId,
            @RequestParam Long tenantId) {

        log.info("Obteniendo redenciones de campaña: {} para tenant: {}", campaignId, tenantId);

        List<CouponRedemption> redemptions = redemptionService.getRedemptionsByCampaign(campaignId, tenantId);
        return ResponseEntity.ok(redemptions);
    }

    /**
     * Obtener redenciones en un rango de fechas.
     */
    @Operation(summary = "Obtener redenciones por rango de fechas",
            description = "Lista redenciones realizadas en un período específico.")
    @GetMapping("/date-range")
    public ResponseEntity<List<CouponRedemption>> getRedemptionsByDateRange(
            @RequestParam Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Obteniendo redenciones para tenant: {} entre {} y {}", tenantId, startDate, endDate);

        List<CouponRedemption> redemptions = redemptionService.getRedemptionsByDateRange(tenantId, startDate, endDate);
        return ResponseEntity.ok(redemptions);
    }

    /**
     * Obtener las últimas N redenciones.
     */
    @Operation(summary = "Obtener redenciones recientes",
            description = "Lista las últimas N redenciones del tenant.")
    @GetMapping("/recent")
    public ResponseEntity<List<CouponRedemption>> getRecentRedemptions(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Obteniendo últimas {} redenciones para tenant: {}", limit, tenantId);

        List<CouponRedemption> redemptions = redemptionService.getRecentRedemptions(tenantId, limit);
        return ResponseEntity.ok(redemptions);
    }
}

