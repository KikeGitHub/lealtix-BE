package com.lealtixservice.service;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.TenantPayment;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TenantPaymentService {
    TenantPayment save(TenantPayment tenantPayment);
    Optional<TenantPayment> findById(Long id);
    List<TenantPayment> findAll();
    void deleteById(Long id);

    TenantPayment intentPayment(PagoDto pagoDto, String status);
}

