package com.lealtixservice.service.impl;

import com.lealtixservice.dto.dashboard.CampaignPerformanceDTO;
import com.lealtixservice.dto.dashboard.CouponStatsDTO;
import com.lealtixservice.dto.dashboard.SalesSummaryDTO;
import com.lealtixservice.dto.dashboard.TimeSeriesCountDTO;
import com.lealtixservice.repository.DashboardCustomerRepository;
import com.lealtixservice.repository.DashboardRedemptionRepository;
import com.lealtixservice.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de dashboard con queries optimizadas para reportes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final DashboardCustomerRepository customerRepository;
    private final DashboardRedemptionRepository redemptionRepository;

    @Override
    public Long getTotalCustomers(Long tenantId, LocalDateTime from, LocalDateTime to) {
        log.debug("Obteniendo total de clientes para tenant {} entre {} y {}", tenantId, from, to);
        return customerRepository.countByTenantAndDateRange(tenantId, from, to);
    }

    @Override
    public List<TimeSeriesCountDTO> getNewCustomersByPeriod(
            Long tenantId,
            String period,
            LocalDateTime from,
            LocalDateTime to
    ) {
        log.debug("Obteniendo clientes nuevos por {} para tenant {} entre {} y {}",
                period, tenantId, from, to);

        // Validar periodo
        if (!List.of("day", "week", "month").contains(period.toLowerCase())) {
            throw new IllegalArgumentException("Period debe ser 'day', 'week' o 'month'");
        }

        List<Object[]> results = customerRepository.findNewCustomersByPeriod(
                tenantId,
                period.toLowerCase(),
                from,
                to
        );

        return results.stream()
                .map(row -> new TimeSeriesCountDTO(
                        ((Date) row[0]).toLocalDate(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    @Override
    public List<CouponStatsDTO> getCouponStats(Long tenantId, LocalDateTime from, LocalDateTime to) {
        log.debug("Obteniendo estadísticas de cupones para tenant {} entre {} y {}", tenantId, from, to);

        List<Object[]> results = redemptionRepository.findCouponStatsByCampaign(tenantId, from, to);

        if (results == null || results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .map(row -> {
                    Long campaignId = ((Number) row[0]).longValue();
                    String campaignName = (String) row[1];
                    Long couponsCreated = ((Number) row[2]).longValue();
                    Long couponsRedeemed = ((Number) row[3]).longValue();
                    Double redemptionRatePct = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;

                    return new CouponStatsDTO(campaignId, campaignName, couponsCreated, couponsRedeemed, redemptionRatePct);
                })
                .toList();
    }

    @Override
    public SalesSummaryDTO getSalesSummary(Long tenantId, LocalDateTime from, LocalDateTime to) {
        log.debug("Obteniendo resumen de ventas para tenant {} entre {} y {}", tenantId, from, to);

        List<Object[]> results = redemptionRepository.findSalesSummary(tenantId, from, to);

        if (results == null || results.isEmpty()) {
            return new SalesSummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, 0L);
        }

        Object[] row = results.get(0);

        BigDecimal totalSales = new BigDecimal(row[0].toString());
        BigDecimal avgTicket = new BigDecimal(row[1].toString());
        Long transactionCount = ((Number) row[2]).longValue();

        return new SalesSummaryDTO(totalSales, avgTicket, transactionCount);
    }

    @Override
    public List<CampaignPerformanceDTO> getCampaignPerformance(
            Long tenantId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        log.debug("Obteniendo rendimiento de campañas para tenant {} entre {} y {}",
                tenantId, from, to);

        List<Object[]> results = redemptionRepository.findCampaignPerformance(tenantId, from, to);

        if (results == null || results.isEmpty()) {
            return List.of();
        }

        return results.stream()
                .map(row -> {
                    Long campaignId = ((Number) row[0]).longValue();
                    String campaignName = (String) row[1];
                    Long couponsIssued = ((Number) row[2]).longValue();
                    Long redemptions = ((Number) row[3]).longValue();
                    BigDecimal totalSales = new BigDecimal(row[4].toString());
                    BigDecimal avgTicket = new BigDecimal(row[5].toString());
                    Double redemptionRatePct = row[6] != null ? ((Number) row[6]).doubleValue() : 0.0;

                    return new CampaignPerformanceDTO(
                            campaignId,
                            campaignName,
                            couponsIssued,
                            redemptions,
                            totalSales,
                            avgTicket,
                            redemptionRatePct
                    );
                })
                .toList();
    }
}

