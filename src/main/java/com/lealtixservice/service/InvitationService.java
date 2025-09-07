package com.lealtixservice.service;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.RegistrationDto;
import com.lealtixservice.dto.ValidateTokenResponse;

/**
 * Interfaz para el servicio de invitaciones.
 */
public interface InvitationService {

    String generateInvitation(PreRegistroDTO dto, String ipAddress);

    ValidateTokenResponse validateToken(String token);

    void completeRegistration(RegistrationDto registrationDto);

}

