package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenantPaymentRepository extends JpaRepository<TenantPayment, Long> {
    TenantPayment findByTenantId(long tenantId);

    TenantPayment findByUIDTenantAndStatus(String UIDTenant, String status);
}
