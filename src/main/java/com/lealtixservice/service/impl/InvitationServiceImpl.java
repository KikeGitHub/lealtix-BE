package com.lealtixservice.service.impl;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.RegistrationDto;
import com.lealtixservice.dto.ValidateTokenResponse;
import com.lealtixservice.entity.Invitation;
import com.lealtixservice.exception.InvalidTokenException;
import com.lealtixservice.repository.InvitationRepository;
import com.lealtixservice.service.InvitationService;
import com.lealtixservice.util.TokenUtils;
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
    private final InvitationRepository invitationRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${invitation.token.expiry-hours}")
    private int expiryHours;

    @Value("${invitation.base-url}")
    private String baseUrl;

    public InvitationServiceImpl(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

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
    @Transactional
    public void completeRegistration(RegistrationDto registrationDto) {
        String tokenHash = TokenUtils.hashToken(registrationDto.getToken());
        Invitation invitation = invitationRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Token no válido"));
        if (invitation.getUsedAt() != null) {
            // Ya fue usado
        }
        if (Instant.now().isAfter(invitation.getExpiresAt())) {
            // Expirado
        }
        invitation.setUsedAt(Instant.now());
        invitationRepository.save(invitation);
        // Aquí se puede continuar con la lógica de registro del tenant y datos de pago.
    }


    private String generateRandomToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
