package com.lealtixservice.service.impl;

import com.lealtixservice.config.SendGridTemplates;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.entity.*;
import com.lealtixservice.repository.*;
import com.lealtixservice.service.Emailservice;
import com.lealtixservice.service.InvitationService;
import com.lealtixservice.service.RegistroService;
import com.lealtixservice.util.EncrypUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Service
public class RegistroServiceImpl implements RegistroService {

    public static final String PENDING = "PENDING";
    @Autowired
    private  AppUserRepository appUserRepository;
    @Autowired
    private  TenantRepository tenantRepository;
    @Autowired
    private  TenantPaymentRepository tenantPaymentRepository;
    @Autowired
    private  RoleRepository roleRepository;
    @Autowired
    private  BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private  InvitationService invitationService;
    @Autowired
    private  Emailservice emailservice;
    @Autowired
    private  PreRegistroRepository preRegistroRepository;
    @Autowired
    private  SendGridTemplates sendGridTemplates;
    @Autowired
    private AppUserRepository userRepository;

    @Override
    @Transactional
    public AppUser register(RegistroDto dto) {
        Invitation invite = invitationService.getInviteByEmail(dto.getEmail());
        AppUser appUser = userRepository.findByEmail(dto.getEmail());
        if (invite == null) {
            throw new IllegalArgumentException("No invitation found for email: " + dto.getEmail());
        }
        if (invite.getUsedAt() != null) {
           if (appUser == null) {
                throw new IllegalArgumentException("User already registered with email: " + dto.getEmail());
            }
        }
        if (Instant.now().isAfter(invite.getExpiresAt())) {
            throw new IllegalArgumentException("Token expired for email: " + dto.getEmail());
        }
        // a) Crear AppUser
        if(appUser != null){
           // modificar usuario existente
           appUser.setFullName(dto.getFullName());
           appUser.setFechaNacimiento(dto.getFechaNacimiento());
           appUser.setTelefono(dto.getTelefono());
           appUser.setPasswordHash(EncrypUtils.encryptPassword(dto.getPassword()));
           appUser.setUpdatedAt(LocalDateTime.now());
        }else{ // nuevo usuario
            appUser = AppUser.builder()
                .fullName(dto.getFullName())
                .fechaNacimiento(dto.getFechaNacimiento())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .passwordHash(EncrypUtils.encryptPassword(dto.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        }
        appUserRepository.save(appUser);

        Role role = roleRepository.findByName("tenant_admin");


        // change status pre-registro to Registered
        PreRegistro preRegistro = preRegistroRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("Pre-registro no encontrado para email: " + dto.getEmail())
        );
        preRegistro.setStatus(PENDING); // Payment pending
        preRegistro.setDescription("Payment pending");
        preRegistro.setUpdatedDate(LocalDateTime.now());
        preRegistroRepository.save(preRegistro);

        return appUser;
    }

    @Override
    public void registrarPago(PagoDto dto) {
        AppUser user = appUserRepository.findByEmail(dto.getEmail());
        Tenant tenant = null;
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + dto.getEmail());
        }
        TenantPayment payment = TenantPayment.builder()
                .tenant(tenant)
                .stripeCustomerId(dto.getStripeCustomerId())
                .stripeSubscriptionId(dto.getStripeSubscriptionId())
                .stripePaymentMethodId(dto.getStripePaymentMethodId())
                .plan(dto.getPlan())
                .status(dto.getStatus())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .name(user.getFullName())
                .build();
        tenantPaymentRepository.save(payment);
    }
}
