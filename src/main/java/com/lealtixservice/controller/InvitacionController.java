package com.lealtixservice.controller;

import com.lealtixservice.dto.InvitacionDTO;
import com.lealtixservice.entity.Invitacion;
import com.lealtixservice.exception.InvalidOrExpiredTokenException;
import com.lealtixservice.service.InvitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitacion")
@RequiredArgsConstructor
public class InvitacionController {
    private final InvitacionService invitacionService;

    @Operation(summary = "Generar y enviar invitación", description = "Genera una invitación para un pre-registro existente y la almacena en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Invitación generada exitosamente"),
        @ApiResponse(responseCode = "404", description = "PreRegistro no encontrado")
    })
    @PostMapping
    public ResponseEntity<?> generarInvitacion(@Validated @RequestBody InvitacionDTO dto) {
        try {
            Invitacion invitacion = invitacionService.generarInvitacion(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(invitacion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Verificar validez de token de invitación", description = "Verifica si el token de invitación es válido y no ha expirado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/verify")
    public ResponseEntity<?> verificarToken(@RequestParam String token) {
        try {
            Invitacion invitacion = invitacionService.verificarToken(token);
            return ResponseEntity.ok(invitacion);
        } catch (InvalidOrExpiredTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
