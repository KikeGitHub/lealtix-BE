package com.lealtixservice.controller;

import com.lealtixservice.dto.CouponCampaignReport;
import com.lealtixservice.dto.CouponSummaryReport;
import com.lealtixservice.service.CouponReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para reportes de cupones y redenciones.
 * Proporciona métricas y estadísticas para análisis de campañas.
 */
@Slf4j
@RestController
@RequestMapping("/api/reports/coupons")
@Tag(name = "Coupon Reports", description = "Reportes y métricas de cupones y redenciones")
@RequiredArgsConstructor
public class CouponReportController {

    private final CouponReportService reportService;

    /**
     * Resumen general de cupones para un tenant.
     */
    @Operation(summary = "Obtener resumen general de cupones",
            description = "Muestra totales de cupones creados, enviados, redimidos, expirados y tasa de redención.")
    @GetMapping("/summary")
    public ResponseEntity<CouponSummaryReport> getSummaryReport(@RequestParam Long tenantId) {
        log.info("Generando reporte resumen para tenant: {}", tenantId);

        CouponSummaryReport report = reportService.getSummaryReport(tenantId);
        return ResponseEntity.ok(report);
    }

    /**
     * Reporte de una campaña específica.
     */
    @Operation(summary = "Obtener reporte por campaña",
            description = "Muestra estadísticas de cupones para una campaña específica.")
    @GetMapping("/by-campaign/{campaignId}")
    public ResponseEntity<CouponCampaignReport> getCampaignReport(
            @PathVariable Long campaignId,
            @RequestParam Long tenantId) {

        log.info("Generando reporte para campaña: {} del tenant: {}", campaignId, tenantId);

        try {
            CouponCampaignReport report = reportService.getCampaignReport(campaignId, tenantId);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            log.error("Error al generar reporte de campaña: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reportes de todas las campañas de un tenant.
     */
    @Operation(summary = "Obtener reportes de todas las campañas",
            description = "Lista reportes de todas las campañas del tenant con sus métricas.")
    @GetMapping("/all-campaigns")
    public ResponseEntity<List<CouponCampaignReport>> getAllCampaignsReport(
            @RequestParam Long tenantId) {

        log.info("Generando reportes de todas las campañas para tenant: {}", tenantId);

        List<CouponCampaignReport> reports = reportService.getAllCampaignsReport(tenantId);
        return ResponseEntity.ok(reports);
    }

    /**
     * Resumen de cupones en un rango de fechas.
     */
    @Operation(summary = "Obtener resumen por rango de fechas",
            description = "Muestra estadísticas de cupones en un período específico.")
    @GetMapping("/summary/date-range")
    public ResponseEntity<CouponSummaryReport> getSummaryReportByDateRange(
            @RequestParam Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Generando reporte resumen para tenant: {} entre {} y {}", tenantId, startDate, endDate);

        CouponSummaryReport report = reportService.getSummaryReportByDateRange(tenantId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    /**
     * Reportes de campañas en un rango de fechas.
     */
    @Operation(summary = "Obtener reportes de campañas por rango de fechas",
            description = "Lista reportes de campañas filtradas por período.")
    @GetMapping("/campaigns/date-range")
    public ResponseEntity<List<CouponCampaignReport>> getCampaignsReportByDateRange(
            @RequestParam Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Generando reportes de campañas para tenant: {} entre {} y {}", tenantId, startDate, endDate);

        List<CouponCampaignReport> reports = reportService.getCampaignsReportByDateRange(tenantId, startDate, endDate);
        return ResponseEntity.ok(reports);
    }
}

