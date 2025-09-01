package com.lealtixservice.controller;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.exception.EmailAlreadyRegisteredException;
import com.lealtixservice.service.PreRegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preregistro")
@RequiredArgsConstructor
public class PreRegistroController {
    private final PreRegistroService preRegistroService;

    @Operation(summary = "Crear pre-registro", description = "Crea un nuevo pre-registro con nombre y email. Retorna conflicto si el email ya está registrado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pre-registro creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<?> crearPreRegistro(@Validated @RequestBody PreRegistroDTO dto) {
        try {
            PreRegistro preRegistro = preRegistroService.register(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(preRegistro);
        } catch (EmailAlreadyRegisteredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
