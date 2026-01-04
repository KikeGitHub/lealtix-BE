package com.lealtixservice.dto.dashboard;

import java.time.LocalDate;

/**
 * DTO de proyección para series de tiempo con conteo.
 * Útil para gráficos de clientes nuevos por día/semana/mes.
 */
public record TimeSeriesCountDTO(
        LocalDate periodStart,
        Long count
) {}

