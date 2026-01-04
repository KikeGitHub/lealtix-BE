package com.lealtixservice.service;

import com.lealtixservice.dto.dashboard.CampaignPerformanceDTO;
import com.lealtixservice.dto.dashboard.CouponStatsDTO;
import com.lealtixservice.dto.dashboard.SalesSummaryDTO;
import com.lealtixservice.dto.dashboard.TimeSeriesCountDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para generar reportes y KPIs del dashboard de negocio.
 */
public interface DashboardService {

    /**
     * KPI 1: Total de clientes registrados por tenant y rango de fechas.
     */
    Long getTotalCustomers(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * KPI 2: Clientes nuevos por periodo (día/semana/mes).
     * @param period 'day', 'week', 'month'
     */
    List<TimeSeriesCountDTO> getNewCustomersByPeriod(
            Long tenantId,
            String period,
            LocalDateTime from,
            LocalDateTime to
    );

    /**
     * KPI 3: Cupones creados vs cupones redimidos.
     */
    List<CouponStatsDTO> getCouponStats(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * KPI 5: Ventas totales generadas por cupones.
     * KPI 6: Ticket promedio por cupón redimido.
     */
    SalesSummaryDTO getSalesSummary(Long tenantId, LocalDateTime from, LocalDateTime to);

    /**
     * KPI 7: Rendimiento por campaña (tabla resumen completa).
     */
    List<CampaignPerformanceDTO> getCampaignPerformance(
            Long tenantId,
            LocalDateTime from,
            LocalDateTime to
    );
}

