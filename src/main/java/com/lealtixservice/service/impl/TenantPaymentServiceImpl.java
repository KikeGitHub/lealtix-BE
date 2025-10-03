package com.lealtixservice.service.impl;

import com.lealtixservice.config.SendGridTemplates;
import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantPayment;
import com.lealtixservice.enums.PaymentStatus;
import com.lealtixservice.repository.AppUserRepository;
import com.lealtixservice.repository.TenantPaymentRepository;
import com.lealtixservice.service.Emailservice;
import com.lealtixservice.service.TenantPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TenantPaymentServiceImpl implements TenantPaymentService {

    private static final Logger log = LoggerFactory.getLogger(TenantPaymentServiceImpl.class);
    @Autowired
    private  TenantPaymentRepository tenantPaymentRepository;

    @Autowired
    private AppUserRepository  appUserRepository;

    @Autowired
    private Emailservice emailservice;

    @Autowired
    private  SendGridTemplates sendGridTemplates;


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
            // activar tenat
            tenant.setActive(true);
            builder.setTenant(tenant);
            // manda email de bienvenida
            EmailDTO emailDTO = EmailDTO.builder()
                .to(pagoDto.getEmail())
                .subject("Bienvenido a Lealtix")
                .templateId(sendGridTemplates.getWelcomeTemplate())
                .dynamicData(Map.of(
                        "name", user.getFullName(),
                        "username", pagoDto.getEmail(),
                        "password", user.getPasswordHash(),
                        "link", "https://app.lealtix.com/login",
                        "logoUrl", "http://cdn.mcauto-images-production.sendgrid.net/b30f9991de8e45d3/af636f80-aa14-4886-9b12-ff4865e26908/627x465.png"
                ))
                .build();
        try {
            emailservice.sendEmailWithTemplate(emailDTO);
        } catch (IOException e) {
            log.error("Error sending welcome email to " + pagoDto.getEmail(), e);
            // implementar bitacora de envios de email
            throw new RuntimeException(e);
        }
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

