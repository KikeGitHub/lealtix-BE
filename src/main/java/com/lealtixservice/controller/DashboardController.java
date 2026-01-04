package com.lealtixservice.controller;

import com.lealtixservice.dto.dashboard.CampaignPerformanceDTO;
import com.lealtixservice.dto.dashboard.CouponStatsDTO;
import com.lealtixservice.dto.dashboard.SalesSummaryDTO;
import com.lealtixservice.dto.dashboard.TimeSeriesCountDTO;
import com.lealtixservice.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para endpoints de dashboard y reportes de negocio.
 * Expone KPIs de clientes, cupones, redenciones y ventas.
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para reportes y KPIs del dashboard de negocio")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "KPI 1: Total de clientes registrados",
               description = "Obtiene el total de clientes registrados en un rango de fechas")
    @GetMapping("/customers/total")
    public ResponseEntity<Long> getTotalCustomers(
            @Parameter(description = "ID del tenant") @RequestParam Long tenantId,
            @Parameter(description = "Fecha inicio (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Fecha fin (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("GET /api/dashboard/customers/total - tenantId={}, from={}, to={}", tenantId, from, to);
        Long total = dashboardService.getTotalCustomers(tenantId, from, to);
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "KPI 2: Clientes nuevos por periodo",
               description = "Obtiene serie de tiempo de clientes nuevos agrupados por día/semana/mes")
    @GetMapping("/customers/new-by-period")
    public ResponseEntity<List<TimeSeriesCountDTO>> getNewCustomersByPeriod(
            @Parameter(description = "ID del tenant") @RequestParam Long tenantId,
            @Parameter(description = "Periodo de agrupación: 'day', 'week', 'month'")
            @RequestParam String period,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("GET /api/dashboard/customers/new-by-period - tenantId={}, period={}, from={}, to={}",
                tenantId, period, from, to);
        List<TimeSeriesCountDTO> timeSeries = dashboardService.getNewCustomersByPeriod(tenantId, period, from, to);
        return ResponseEntity.ok(timeSeries);
    }

    @Operation(summary = "KPI 3: Cupones creados vs redimidos",
               description = "Obtiene estadísticas de cupones emitidos y redimidos por campaña")
    @GetMapping("/coupons/stats")
    public ResponseEntity<List<CouponStatsDTO>> getCouponStats(
            @Parameter(description = "ID del tenant") @RequestParam Long tenantId,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("GET /api/dashboard/coupons/stats - tenantId={}, from={}, to={}", tenantId, from, to);
        List<CouponStatsDTO> stats = dashboardService.getCouponStats(tenantId, from, to);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "KPI 5 y 6: Resumen de ventas",
               description = "Obtiene total de ventas, ticket promedio y cantidad de transacciones de cupones")
    @GetMapping("/sales/summary")
    public ResponseEntity<SalesSummaryDTO> getSalesSummary(
            @Parameter(description = "ID del tenant") @RequestParam Long tenantId,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("GET /api/dashboard/sales/summary - tenantId={}, from={}, to={}", tenantId, from, to);
        SalesSummaryDTO summary = dashboardService.getSalesSummary(tenantId, from, to);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "KPI 7: Rendimiento por campaña",
               description = "Obtiene tabla resumen completa de performance de campañas con todas las métricas")
    @GetMapping("/campaigns/performance")
    public ResponseEntity<List<CampaignPerformanceDTO>> getCampaignPerformance(
            @Parameter(description = "ID del tenant") @RequestParam Long tenantId,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        log.info("GET /api/dashboard/campaigns/performance - tenantId={}, from={}, to={}", tenantId, from, to);
        List<CampaignPerformanceDTO> performance = dashboardService.getCampaignPerformance(tenantId, from, to);
        return ResponseEntity.ok(performance);
    }
}

