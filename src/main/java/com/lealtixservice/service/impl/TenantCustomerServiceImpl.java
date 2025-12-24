package com.lealtixservice.service.impl;

import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantCustomer;
import com.lealtixservice.repository.TenantCustomerRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.service.Emailservice;
import com.lealtixservice.service.TenantCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TenantCustomerServiceImpl implements TenantCustomerService {

    @Autowired
    private TenantCustomerRepository tenantCustomerRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private Emailservice emailService;

    @Value("${sendgrid.templates.welcome-customer}")
    private String welcomeTemplateId;

    @Override
    public TenantCustomer save(TenantCustomer customer) {

        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        TenantCustomer saved = tenantCustomerRepository.save(customer);

        // Enviar correo de bienvenida usando SendGrid si el guardado fue exitoso
        try {
            Tenant tenant = null;
            if (customer.getTenant() != null && customer.getTenant().getId() != null) {
                tenant = tenantRepository.findById(customer.getTenant().getId()).orElse(null);
            } else if (saved.getTenant() != null && saved.getTenant().getId() != null) {
                tenant = tenantRepository.findById(saved.getTenant().getId()).orElse(null);
            }

            Map<String, Object> dynamicData = new HashMap<>();
            if (tenant != null) {
                dynamicData.put("tenantName", tenant.getNombreNegocio());
                dynamicData.put("logoUrl", tenant.getLogoUrl());
            } else {
                dynamicData.put("tenantName", "");
                dynamicData.put("logoUrl", "");
            }

            dynamicData.put("customerName", saved.getName());
            // Valores dummy por ahora
            dynamicData.put("discount", "10");
            dynamicData.put("couponCode", "9879oiolklk");
            String landingUrl = "http://localhost:4200/landing-page?token=" + (tenant != null && tenant.getSlug() != null ? tenant.getSlug() : "cafecito-lindo-y-querido-14");
            dynamicData.put("landingUrl", landingUrl);

            EmailDTO emailDTO = EmailDTO.builder()
                    .to(saved.getEmail())
                    .subject("Bienvenido a " + (tenant != null ? tenant.getNombreNegocio() : "nuestro servicio"))
                    .templateId(welcomeTemplateId)
                    .dynamicData(dynamicData)
                    .build();

            emailService.sendEmailWithTemplate(emailDTO);
        } catch (IOException ex) {
            log.error("Error sending welcome email to customer: {}", saved.getEmail(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error sending welcome email", ex);
        }

        return saved;
    }

    @Override
    public Optional<TenantCustomer> findById(Long id) {
        return tenantCustomerRepository.findById(id);
    }

    @Override
    public List<TenantCustomer> findAll() {
        return tenantCustomerRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        tenantCustomerRepository.deleteById(id);
    }

    @Override
    public List<TenantCustomer> findByTenantId(Long tenantId) {
        return tenantCustomerRepository.findByTenantId(tenantId);
    }
}
