package com.lealtixservice.service.impl;

import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.entity.*;
import com.lealtixservice.repository.AppUserRepository;
import com.lealtixservice.repository.TenantRepository;
import com.lealtixservice.repository.TenantUserRepository;
import com.lealtixservice.repository.TenantPaymentRepository;
import com.lealtixservice.repository.RoleRepository;
import com.lealtixservice.service.InvitationService;
import com.lealtixservice.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RegistroServiceImpl implements RegistroService {

    private final AppUserRepository appUserRepository;
    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final TenantPaymentRepository tenantPaymentRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final InvitationService invitationService;

    @Autowired
    public RegistroServiceImpl(AppUserRepository appUserRepository,
                              TenantRepository tenantRepository,
                              TenantUserRepository tenantUserRepository,
                              TenantPaymentRepository tenantPaymentRepository,
                              RoleRepository roleRepository, InvitationService invitationService) {
        this.appUserRepository = appUserRepository;
        this.tenantRepository = tenantRepository;
        this.tenantUserRepository = tenantUserRepository;
        this.tenantPaymentRepository = tenantPaymentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.invitationService = invitationService;
    }

    @Override
    @Transactional
    public void register(RegistroDto dto) {
        Invitation invite = invitationService.getInviteByEmail(dto.getEmail());
        if (invite == null) {
            throw new IllegalArgumentException("No invitation found for email: " + dto.getEmail());
        }
        if (invite.getUsedAt() != null) {
            throw new IllegalArgumentException("Invitation already used for email: " + dto.getEmail());
        }
        if (Instant.now().isAfter(invite.getExpiresAt())) {
            throw new IllegalArgumentException("Token expired for email: " + dto.getEmail());
        }
        // a) Crear AppUser
        AppUser user = AppUser.builder()
                .nombre(dto.getNombre())
                .paterno(dto.getPaterno())
                .materno(dto.getMaterno())
                .fechaNacimiento(dto.getFechaNacimiento())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .build();
        appUserRepository.save(user);

        // b) Guardar Tenant
        Tenant tenant = Tenant.builder()
                .nombreNegocio(dto.getNombreNegocio())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefonoNegocio())
                .tipoNegocio(dto.getTipoNegocio())
                .build();
        tenantRepository.save(tenant);

        // c) Obtener rol "tenant_admin"
        Role role = roleRepository.findByName("tenant_admin");

        // d) Crear TenantUserId y TenantUser
        TenantUserId tenantUserId = new TenantUserId(
            tenant.getId(),
            user.getId(),
            role.getId()
        );
        TenantUser tenantUser = TenantUser.builder()
            .id(tenantUserId)
            .user(user)
            .tenant(tenant)
            .role(role)
            .build();
        tenantUserRepository.save(tenantUser);

        // e) Crear TenantPayment
        TenantPayment payment = TenantPayment.builder()
                .tenant(tenant)
                .plan(dto.getPlan())
                .status(dto.getStatus())
                .build();

        tenantPaymentRepository.save(payment);

        // confirm token invitation
        invite.setUsedAt(Instant.now());
        invitationService.save(invite);

    }
}
