package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantPaymentRepository extends JpaRepository<TenantPayment, Long> {
}

