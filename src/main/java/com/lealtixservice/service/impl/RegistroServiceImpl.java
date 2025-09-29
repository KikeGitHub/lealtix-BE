package com.lealtixservice.service.impl;

import com.lealtixservice.config.SendGridTemplates;
import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.entity.*;
import com.lealtixservice.enums.PaymentStatus;
import com.lealtixservice.repository.*;
import com.lealtixservice.service.Emailservice;
import com.lealtixservice.service.InvitationService;
import com.lealtixservice.service.RegistroService;
import com.lealtixservice.service.TenantPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class RegistroServiceImpl implements RegistroService {

    public static final String PENDING = "PENDING";
    @Autowired
    private  AppUserRepository appUserRepository;
    @Autowired
    private  TenantRepository tenantRepository;
    @Autowired
    private  TenantUserRepository tenantUserRepository;
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
    public Tenant register(RegistroDto dto) {
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
           appUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }else{ // nuevo usuario
            appUser = AppUser.builder()
                .fullName(dto.getFullName())
                .fechaNacimiento(dto.getFechaNacimiento())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();
        }
        appUserRepository.save(appUser);

        // b) Guardar Tenant
        // valida si ya existe un tenant
        Tenant tenant = Tenant.builder().build();
        TenantUser existingTenantUser = tenantUserRepository.findByUserId(appUser.getId());
        if (existingTenantUser != null && existingTenantUser.getTenant() != null) {
            tenant = existingTenantUser.getTenant();  // modificar tenant existente
            tenant.setNombreNegocio(dto.getNombreNegocio());
            tenant.setDireccion(dto.getDireccion());
            tenant.setTelefono(dto.getTelefonoNegocio());
            tenant.setTipoNegocio(dto.getTipoNegocio());
        }else{ // nuevo tenant
            tenant = Tenant.builder()
                    .nombreNegocio(dto.getNombreNegocio())
                    .direccion(dto.getDireccion())
                    .isActive(false) // initially inactive until payment is confirmed
                    .telefono(dto.getTelefonoNegocio())
                    .tipoNegocio(dto.getTipoNegocio())
                    .build();
        }
        tenantRepository.save(tenant);
        String UIDTenant = generateUID(tenant);
        tenant.setUIDTenant(UIDTenant);
        tenantRepository.save(tenant);

        // c) Obtener rol "tenant_admin"
        Role role = roleRepository.findByName("tenant_admin");

        // d) Crear TenantUserId y TenantUser
        TenantUserId tenantUserId = new TenantUserId(
            tenant.getId(),
            appUser.getId(),
            role.getId()
        );
        TenantUser tenantUser = TenantUser.builder()
            .id(tenantUserId)
            .user(appUser)
            .tenant(tenant)
            .role(role)
            .build();
        tenantUserRepository.save(tenantUser);

        // confirm token invitation
        invite.setUsedAt(Instant.now());
        invitationService.save(invite);

        // change status pre-registro to Registered
        PreRegistro preRegistro = preRegistroRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("Pre-registro no encontrado para email: " + dto.getEmail())
        );
        preRegistro.setStatus(PENDING); // Payment pending
        preRegistro.setDescription("Payment pending");
        preRegistro.setUpdatedDate(LocalDateTime.now());
        preRegistroRepository.save(preRegistro);

        // Initiate payment process INITIATED Status
        TenantPayment tenantPayment = tenantPaymentRepository.findByUIDTenantAndStatus(UIDTenant, "INITIATED");
        if(tenantPayment == null){
            tenantPayment = TenantPayment.builder()
                    .tenant(tenant)
                    .plan(dto.getPlan())
                    .status(PaymentStatus.INITIATED.getStatus())
                    .name(appUser.getFullName())
                    .description("Payment initiated")
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusMonths(1)) // assuming monthly plan
                    .UIDTenant(generateUID(tenant))
                    .userEmail(dto.getEmail())
                    .build();
        }else{
            tenantPayment.setName(appUser.getFullName());
            tenantPayment.setDescription("Payment initiated");
            tenantPayment.setStartDate(LocalDateTime.now());
            tenantPayment.setEndDate(LocalDateTime.now().plusMonths(1));
        }

        tenantPaymentRepository.save(tenantPayment);

        return tenant;
    }

    private String generateUID(Tenant tenant) {
        String namePart = tenant.getNombreNegocio().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        String uniqueId = String.valueOf(tenant.getId());
        return namePart + "-" + uniqueId;
    }

    @Override
    public void registrarPago(PagoDto dto) {
        AppUser user = appUserRepository.findByEmail(dto.getEmail());
        Tenant tenant = null;
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + dto.getEmail());
        }else{
            TenantUser tenantUser = tenantUserRepository.findByUserId(user.getId());
            if(tenantUser == null || tenantUser.getTenant() == null){
                throw new IllegalArgumentException("The user is not associated with any tenant");
            }
            tenant = tenantUser.getTenant();
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
