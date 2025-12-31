package com.lealtixservice.service.impl;

import com.lealtixservice.dto.CouponCampaignReport;
import com.lealtixservice.dto.CouponSummaryReport;
import com.lealtixservice.entity.Campaign;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.enums.CouponStatus;
import com.lealtixservice.repository.CampaignRepository;
import com.lealtixservice.repository.CouponRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.service.CouponReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio de reportes de cupones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponReportServiceImpl implements CouponReportService {

    private final CouponRepository couponRepository;
    private final CampaignRepository campaignRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public CouponSummaryReport getSummaryReport(Long tenantId) {
        log.debug("Generando reporte resumen para tenant: {}", tenantId);

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant no encontrado"));

        // Obtener todas las campañas del tenant
        List<Campaign> campaigns = campaignRepository.findByBusinessId(tenantId);

        long totalCreated = 0;
        long totalSent = 0;
        long totalRedeemed = 0;
        long totalExpired = 0;
        long totalCancelled = 0;
        long totalActive = 0;

        // Sumar cupones de todas las campañas
        for (Campaign campaign : campaigns) {
            totalCreated += couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.CREATED);
            totalSent += couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.SENT);
            totalRedeemed += couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.REDEEMED);
            totalExpired += couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.EXPIRED);
            totalCancelled += couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.CANCELLED);
            totalActive += couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.ACTIVE);
        }

        CouponSummaryReport report = CouponSummaryReport.builder()
                .tenantId(tenantId)
                .tenantName(tenant.getNombreNegocio())
                .totalCreated(totalCreated)
                .totalSent(totalSent)
                .totalRedeemed(totalRedeemed)
                .totalExpired(totalExpired)
                .totalCancelled(totalCancelled)
                .totalActive(totalActive)
                .build();

        report.calculateRedemptionRate();

        log.info("Reporte generado para tenant {}: {} enviados, {} redimidos ({}%)",
                tenantId, totalSent, totalRedeemed, report.getRedemptionRate());

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public CouponCampaignReport getCampaignReport(Long campaignId, Long tenantId) {
        log.debug("Generando reporte para campaña: {} del tenant: {}", campaignId, tenantId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaña no encontrada"));

        // Validar que la campaña pertenece al tenant
        if (!campaign.getBusinessId().equals(tenantId)) {
            throw new IllegalArgumentException("La campaña no pertenece a este tenant");
        }

        long totalSent = couponRepository.countByCampaignIdAndStatus(campaignId, CouponStatus.SENT);
        long totalRedeemed = couponRepository.countByCampaignIdAndStatus(campaignId, CouponStatus.REDEEMED);
        long totalActive = couponRepository.countByCampaignIdAndStatus(campaignId, CouponStatus.ACTIVE);
        long totalExpired = couponRepository.countByCampaignIdAndStatus(campaignId, CouponStatus.EXPIRED);

        CouponCampaignReport report = CouponCampaignReport.builder()
                .campaignId(campaignId)
                .campaignTitle(campaign.getTitle())
                .campaignDescription(campaign.getDescription())
                .totalSent(totalSent)
                .totalRedeemed(totalRedeemed)
                .totalActive(totalActive)
                .totalExpired(totalExpired)
                .build();

        report.calculateRedemptionRate();

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponCampaignReport> getAllCampaignsReport(Long tenantId) {
        log.debug("Generando reportes de todas las campañas para tenant: {}", tenantId);

        List<Campaign> campaigns = campaignRepository.findByBusinessId(tenantId);
        List<CouponCampaignReport> reports = new ArrayList<>();

        for (Campaign campaign : campaigns) {
            long totalSent = couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.SENT);
            long totalRedeemed = couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.REDEEMED);
            long totalActive = couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.ACTIVE);
            long totalExpired = couponRepository.countByCampaignIdAndStatus(campaign.getId(), CouponStatus.EXPIRED);

            CouponCampaignReport report = CouponCampaignReport.builder()
                    .campaignId(campaign.getId())
                    .campaignTitle(campaign.getTitle())
                    .campaignDescription(campaign.getDescription())
                    .totalSent(totalSent)
                    .totalRedeemed(totalRedeemed)
                    .totalActive(totalActive)
                    .totalExpired(totalExpired)
                    .build();

            report.calculateRedemptionRate();
            reports.add(report);
        }

        return reports;
    }

    @Override
    @Transactional(readOnly = true)
    public CouponSummaryReport getSummaryReportByDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Generando reporte resumen para tenant: {} entre {} y {}", tenantId, startDate, endDate);

        // Nota: Para implementar filtrado por fechas, necesitarías agregar queries específicas
        // en CouponRepository que filtren por fecha de creación o redención
        // Por ahora, retornamos el reporte general
        // TODO: Implementar queries específicas con filtrado de fechas

        return getSummaryReport(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponCampaignReport> getCampaignsReportByDateRange(Long tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Generando reportes de campañas para tenant: {} entre {} y {}", tenantId, startDate, endDate);

        // TODO: Implementar queries específicas con filtrado de fechas

        return getAllCampaignsReport(tenantId);
    }
}

