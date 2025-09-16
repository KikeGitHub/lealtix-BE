package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.exception.EmailAlreadyRegisteredException;
import com.lealtixservice.service.PreRegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/preregistro")
@RequiredArgsConstructor
public class PreRegistroController {

    @Autowired
    private PreRegistroService preRegistroService;

    @Operation(summary = "Crear pre-registro", description = "Crea un nuevo pre-registro con nombre y email. Retorna conflicto si el email ya está registrado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pre-registro creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<GenericResponse> crearPreRegistro(@Validated @RequestBody PreRegistroDTO dto) {
        try {
            PreRegistro preRegistro = preRegistroService.register(dto);
            return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse(201, "SUCCESS", preRegistro));
        } catch (EmailAlreadyRegisteredException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new GenericResponse(409, e.getMessage(), null));
        } catch (IOException e) {
            log.error("Error enviando email de pre-registro", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse(500, "Error enviando email de pre-registro", null));
        }
    }
    

    @Operation(summary = "Eliminar pre-registro", description = "Elimina un pre-registro por id. Retorna 204 si se elimina, 404 si no existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pre-registro eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pre-registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deletePreRegistro(@PathVariable Long id) {
        try {
            preRegistroService.deletePreRegistro(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new GenericResponse(204, "SUCCESS", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResponse(404, e.getMessage(), null));
        }
    }

    @Operation(summary = "Obtener pre-registro por email", description = "Retorna el pre-registro asociado al email. 404 si no existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pre-registro encontrado"),
        @ApiResponse(responseCode = "404", description = "Pre-registro no encontrado")
    })
    @GetMapping
    public ResponseEntity<GenericResponse> getPreRegistroByEmail(@RequestParam String email) {
        PreRegistro preRegistro = preRegistroService.getPreRegistroByEmail(email);
        if (preRegistro != null) {
            return ResponseEntity.ok(new GenericResponse(200, "SUCCESS", preRegistro));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResponse(404, "Pre-registro no encontrado", null));
        }
    }
}
