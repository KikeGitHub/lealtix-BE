package com.lealtixservice.service.impl;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantPayment;
import com.lealtixservice.entity.TenantUser;
import com.lealtixservice.enums.PaymentStatus;
import com.lealtixservice.repository.AppUserRepository;
import com.lealtixservice.repository.TenantPaymentRepository;
import com.lealtixservice.repository.TenantUserRepository;
import com.lealtixservice.service.TenantPaymentService;
import com.lealtixservice.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TenantPaymentServiceImpl implements TenantPaymentService {

    @Autowired
    private  TenantPaymentRepository tenantPaymentRepository;

    @Autowired
    private AppUserRepository  appUserRepository;

    @Autowired
    private TenantUserRepository tenantUserRepository;


    @Override
    public TenantPayment save(TenantPayment tenantPayment) {
        return tenantPaymentRepository.save(tenantPayment);
    }

    @Override
    public Optional<TenantPayment> findById(Long id) {
        return tenantPaymentRepository.findById(id);
    }

    @Override
    public List<TenantPayment> findAll() {
        return tenantPaymentRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        tenantPaymentRepository.deleteById(id);
    }

    @Override
    public TenantPayment intentPayment(PagoDto pagoDto, String status) {
        AppUser user = appUserRepository.findByEmail(pagoDto.getEmail());
        Tenant tenant = null;
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + pagoDto.getEmail());
        }else{
            TenantUser tenantUser = tenantUserRepository.findByUserId(user.getId());
            if(tenantUser == null){
                throw new IllegalArgumentException("El usuario no está asociado a ningún tenant " + pagoDto.getEmail());
            }
            tenant = tenantUser.getTenant();
        }
        PaymentStatus paymentStatus;
        try {
            paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estatus de pago inválido " + pagoDto.getEmail());
        }

        TenantPayment builder = new TenantPayment();
        builder.setTenant(tenant);
        builder.setCreatedAt(LocalDateTime.now());
        builder.setPlan(pagoDto.getPlan());
        builder.setStatus(paymentStatus.getStatus());

        String description;
        if (paymentStatus == PaymentStatus.SUCCESS) {
            builder.setStripeCustomerId(pagoDto.getStripeCustomerId());
            builder.setStripeSubscriptionId(pagoDto.getStripeSubscriptionId());
            builder.setStripePaymentMethodId(pagoDto.getStripePaymentMethodId());
            builder.setStartDate(LocalDateTime.now());
            builder.setEndDate(LocalDateTime.now().plusMonths(1));
            builder.setDescription("El pago se realizo con exito");
        } else if (paymentStatus == PaymentStatus.FAILED) {
            builder.setDescription("Fallo el pago");
        } else if (paymentStatus == PaymentStatus.CANCELED) {
            builder.setDescription("El pago fue cancelado");
        } else {
            builder.setDescription("Intento de pago iniciado");
        }
        tenantPaymentRepository.save(builder);

        return builder;
    }
}

