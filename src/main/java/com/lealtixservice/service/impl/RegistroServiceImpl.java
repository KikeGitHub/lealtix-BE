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
    public void register(RegistroDto dto) {
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
           appUser.setNombre(dto.getNombre());
           appUser.setPaterno(dto.getPaterno());
           appUser.setMaterno(dto.getMaterno());
           appUser.setFechaNacimiento(dto.getFechaNacimiento());
           appUser.setTelefono(dto.getTelefono());
           appUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }else{ // nuevo usuario
            appUser = AppUser.builder()
                .nombre(dto.getNombre())
                .paterno(dto.getPaterno())
                .materno(dto.getMaterno())
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
        TenantPayment tenantPayment = TenantPayment.builder()
                .tenant(tenant)
                .plan(dto.getPlan())
                .status(PaymentStatus.INITIATED.getStatus())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1)) // assuming monthly plan
                .build();
        tenantPaymentRepository.save(tenantPayment);

        // send welcome email could be here NO debe enviar email hasya confirmar pago.
//        EmailDTO emailDTO = EmailDTO.builder()
//                .to(dto.getEmail())
//                .subject("Bienvenido a Lealtix")
//                .templateId(sendGridTemplates.getWelcomeTemplate())
//                .dynamicData(Map.of(
//                        "name", dto.getNombre(),
//                        "username", dto.getEmail(),
//                        "password", dto.getPassword(),
//                        "link", "https://app.lealtix.com/login",
//                        "logoUrl", "http://cdn.mcauto-images-production.sendgrid.net/b30f9991de8e45d3/af636f80-aa14-4886-9b12-ff4865e26908/627x465.png"
//                ))
//                .build();
//        try {
//            emailservice.sendEmailWithTemplate(emailDTO);
//        } catch (IOException e) {
//            log.error("Error sending welcome email to " + dto.getEmail(), e);
//            // implementar bitacora de envios de email
//            throw new RuntimeException(e);
//        }


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
                .build();
        tenantPaymentRepository.save(payment);
    }
}
