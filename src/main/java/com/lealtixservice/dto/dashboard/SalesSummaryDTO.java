package com.lealtixservice.dto.dashboard;

import java.math.BigDecimal;

/**
 * DTO de proyección para resumen de ventas generadas por cupones.
 * Contiene totales, promedios y contadores de transacciones.
 */
public record SalesSummaryDTO(
        BigDecimal totalSales,
        BigDecimal avgTicket,
        Long transactionCount
) {}

