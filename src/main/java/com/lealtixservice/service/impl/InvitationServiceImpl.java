package com.lealtixservice.service.impl;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.ValidateTokenResponse;
import com.lealtixservice.entity.Invitation;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.repository.InvitationRepository;
import com.lealtixservice.service.InvitationService;
import com.lealtixservice.service.PreRegistroService;
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
    private PreRegistroService preRegistroService;

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
        String email = null;

        String tokenHash = TokenUtils.hashToken(token);
        Invitation invitation = invitationRepository.findByTokenHash(tokenHash).orElse(null);
        if(invitation == null){
            message = "Token no válido";
            ok = false;
        }else if (invitation.getUsedAt() != null) {
            message = "Token ya usado";
            ok = false;
        }else if (Instant.now().isAfter(invitation.getExpiresAt())) {
            message = "Token expirado";
            ok = false;
        }else{
            email = invitation.getEmail();
        }
        return ValidateTokenResponse.builder().ok(ok).email(email).message(message).build();
    }


    @Override
    public Invitation getInviteByEmail(String email) {
        return (Invitation) invitationRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void save(Invitation invite) {
        invitationRepository.save(invite);
    }

    @Override
    public void markPreRegistroAsRegistered(String email) {
        PreRegistro preRegistro = preRegistroService.getPreRegistroByEmail(email);
        if (preRegistro != null) {
            preRegistro.setStatus("Registered");
            preRegistroService.save(preRegistro);
        }else{
            throw new IllegalArgumentException("No pre-registro found for email: " + email);
        }
    }

    private String generateRandomToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
