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

    @Operation(summary = "Eliminar pre-registro", description = "Elimina un pre-registro por id. Retorna 204 si se elimina, 404 si no existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pre-registro eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pre-registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreRegistro(@PathVariable Long id) {
        try {
            preRegistroService.deletePreRegistro(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener pre-registro por email", description = "Retorna el pre-registro asociado al email. 404 si no existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pre-registro encontrado"),
        @ApiResponse(responseCode = "404", description = "Pre-registro no encontrado")
    })
    @GetMapping
    public ResponseEntity<?> getPreRegistroByEmail(@RequestParam String email) {
        PreRegistro preRegistro = preRegistroService.getPreRegistroByEmail(email);
        if (preRegistro != null) {
            return ResponseEntity.ok(preRegistro);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pre-registro no encontrado");
        }
    }
}
