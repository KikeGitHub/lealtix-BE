package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.dto.ValidateTokenResponse;
import com.lealtixservice.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Generar invitación", description = "Genera una invitación para el registro de un tenant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitación enviada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping("/invite")
    public ResponseEntity<GenericResponse> invite(@RequestBody PreRegistroDTO dto, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String invite = invitationService.generateInvitation(dto, ipAddress);
        log.info("invite: {}", invite);
        return ResponseEntity.ok(new GenericResponse(200, "SUCCESS", "Invitación enviada a " + dto.getEmail()));
    }

    /**
     * Endpoint para validar un token.
     */
    @Operation(summary = "Validar token de invitación", description = "Valida el token de invitación para el registro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "400", description = "Token inválido")
    })
    @GetMapping("/validate-token")
    public ResponseEntity<GenericResponse> validateToken(@RequestParam String token) {
        try {
            ValidateTokenResponse response = invitationService.validateToken(token);
            if (response != null && response.isOk()) {
                return ResponseEntity.ok(new GenericResponse(200, "SUCCESS", response));
            } else {
                return ResponseEntity.status(400).body(new GenericResponse(400, "Token inválido o expirado", response));
            }
        } catch (Exception e) {
            log.error("Error logging token: {}", e.getMessage());
            return ResponseEntity.status(400).body(new GenericResponse(400, "Token inválido o expirado", null));
        }
    }
}
