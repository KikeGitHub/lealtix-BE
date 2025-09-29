package com.lealtixservice.service.impl;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.dto.ValidateTokenResponse;
import com.lealtixservice.entity.*;
import com.lealtixservice.repository.*;
import com.lealtixservice.service.InvitationService;
import com.lealtixservice.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

/**
 * Implementación del servicio de invitaciones.
 */
@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private PreRegistroRepository preRegistroRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TenantUserRepository tenantUserRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${invitation.token.expiry-hours}")
    private int expiryHours;

    @Value("${invitation.base-url}")
    private String baseUrl;


    @Override
    @Transactional
    public String generateInvitation(PreRegistroDTO dto, String ipAddress) {

        String token = generateRandomToken();
        String tokenHash = TokenUtils.hashToken(token);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofHours(expiryHours));

        Invitation invitation = new Invitation();
        invitation.setEmail(dto.getEmail());
        invitation.setTokenHash(tokenHash);
        invitation.setCreatedAt(now);
        invitation.setExpiresAt(expiresAt);
        invitation.setCreatedByIp(ipAddress);

        invitationRepository.save(invitation);

        return baseUrl + "/registro?register=true&token=" + token;
    }

    @Override
    public ValidateTokenResponse validateToken(String token) {
        String message = "SUCCESS";
        boolean ok = true;
        RegistroDto registroDto = new RegistroDto();
        ValidateTokenResponse response = ValidateTokenResponse.builder().build();

        String tokenHash = TokenUtils.hashToken(token);
        Invitation invitation = invitationRepository.findByTokenHash(tokenHash).orElse(null);
        if(invitation == null){
            message = "Token no válido";
            ok = false;
        }else if (invitation.getUsedAt() != null) {
            AppUser user = appUserRepository.findByEmail(invitation.getEmail());
            if(user == null){
                message = "Token ya usado sin Registro de Tenant";
                ok = false;
            }else{
                TenantUser tenantUser = tenantUserRepository.findByUserId(user.getId());
                if(tenantUser.getTenant() != null){
                    Tenant tenant = tenantUser.getTenant();
                    // Token usado con registro de tenant mapear todo el objeto
                    registroDto.setFullName(user.getFullName());
                    registroDto.setFechaNacimiento(user.getFechaNacimiento());
                    registroDto.setTelefono(user.getTelefono());
                    registroDto.setEmail(user.getEmail());
                    registroDto.setNombreNegocio(tenant.getNombreNegocio());
                    registroDto.setDireccion(tenant.getDireccion());
                    registroDto.setTelefonoNegocio(tenant.getTelefono());
                    registroDto.setTipoNegocio(tenant.getTipoNegocio());
                    response.setRegistroDto(registroDto);
                }else{
                    message = "Token ya usado Sin Registro de Tenant";
                    ok = false;
                }
            }

        }else if (Instant.now().isAfter(invitation.getExpiresAt())) {
            message = "Token expirado";
            ok = false;
        }else{
            response.setEmail(invitation.getEmail());
            PreRegistro preRegistro = preRegistroRepository.findByEmail(invitation.getEmail()).orElse(null);
            if(preRegistro != null) {
                registroDto.setFullName(preRegistro.getNombre());
                registroDto.setEmail(preRegistro.getEmail());
                response.setRegistroDto(registroDto);
            }
        }
        response.setOk(ok);
        response.setMessage(message);

        return response;
    }


    @Override
    public Invitation getInviteByEmail(String email) {
        return (Invitation) invitationRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void save(Invitation invite) {
        invitationRepository.save(invite);
    }


    private String generateRandomToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
