package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Extensión del repositorio de TenantCustomer con queries para dashboard y reportes.
 */
@Repository
public interface DashboardCustomerRepository extends JpaRepository<TenantCustomer, Long> {

    /**
     * Cuenta clientes registrados por tenant en un rango de fechas.
     */
    @Query("SELECT COUNT(c) FROM TenantCustomer c " +
           "WHERE c.tenant.id = :tenantId " +
           "AND c.createdAt BETWEEN :from AND :to")
    Long countByTenantAndDateRange(
            @Param("tenantId") Long tenantId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /**
     * Clientes nuevos agrupados por periodo (día/semana/mes).
     * Usa SQL nativo con date_trunc de PostgreSQL.
     */
    @Query(value = """
            SELECT CAST(date_trunc(:period, created_at) AS DATE) AS periodStart,
                   COUNT(*) AS count
            FROM tenant_customer
            WHERE tenant_id = :tenantId
              AND created_at BETWEEN :from AND :to
            GROUP BY periodStart
            ORDER BY periodStart
            """, nativeQuery = true)
    List<Object[]> findNewCustomersByPeriod(
            @Param("tenantId") Long tenantId,
            @Param("period") String period, // 'day', 'week', 'month'
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}

