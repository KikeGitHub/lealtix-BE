package com.lealtixservice.service;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.dto.ValidateTokenResponse;
import com.lealtixservice.entity.Invitation;

/**
 * Interfaz para el servicio de invitaciones.
 */
public interface InvitationService {

    String generateInvitation(PreRegistroDTO dto, String ipAddress);

    ValidateTokenResponse validateToken(String token);

    Invitation getInviteByEmail(String email);

    void save(Invitation invite);
}

