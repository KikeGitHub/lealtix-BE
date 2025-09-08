package com.lealtixservice.controller;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.dto.ValidateTokenResponse;
import com.lealtixservice.service.InvitationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para el flujo de invitación de tenant.
 */
@Slf4j
@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * Endpoint para generar una invitación.
     */
    @PostMapping("/invite")
    public ResponseEntity<?> invite(@RequestBody PreRegistroDTO dto, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String invite = invitationService.generateInvitation(dto, ipAddress);
        log.info("invite: {}", invite);
        return ResponseEntity.ok().body("Invitación enviada a " + dto.getEmail());
    }

    /**
     * Endpoint para validar un token.
     */
    @GetMapping("/validate-token")
    public ResponseEntity<ValidateTokenResponse> validateToken(@RequestParam String token) {
        ValidateTokenResponse response = invitationService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}
