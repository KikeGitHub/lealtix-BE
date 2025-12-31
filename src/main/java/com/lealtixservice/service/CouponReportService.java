package com.lealtixservice.service;

import com.lealtixservice.dto.CouponCampaignReport;
import com.lealtixservice.dto.CouponSummaryReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para generar reportes de cupones y redenciones.
 */
public interface CouponReportService {

    /**
     * Genera un resumen general de cupones para un tenant.
     *
     * @param tenantId ID del tenant
     * @return Reporte resumen
     */
    CouponSummaryReport getSummaryReport(Long tenantId);

    /**
     * Genera un reporte de cupones para una campaña específica.
     *
     * @param campaignId ID de la campaña
     * @param tenantId ID del tenant (validación)
     * @return Reporte de campaña
     */
    CouponCampaignReport getCampaignReport(Long campaignId, Long tenantId);

    /**
     * Genera reportes de todas las campañas de un tenant.
     *
     * @param tenantId ID del tenant
     * @return Lista de reportes por campaña
     */
    List<CouponCampaignReport> getAllCampaignsReport(Long tenantId);

    /**
     * Genera un resumen de redenciones en un rango de fechas.
     *
     * @param tenantId ID del tenant
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return Reporte resumen
     */
    CouponSummaryReport getSummaryReportByDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Genera reportes de campañas en un rango de fechas.
     *
     * @param tenantId ID del tenant
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return Lista de reportes por campaña
     */
    List<CouponCampaignReport> getCampaignsReportByDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate);
}

