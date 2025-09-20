package com.lealtixservice.controller;

import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/registro")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario y negocio en el sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registro exitoso"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(@RequestBody RegistroDto registroDto) {
       try {
            Tenant tenant=  registroService.register(registroDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse(200, "SUCCESS", tenant.getUIDTenant()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse(500, e.getMessage(), null));
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error during registration";
            log.error("Error during registration: {}", errorMsg, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponse(400, "Invalid request body", null));
        }
    }

    @Operation(summary = "Registrar pago", description = "Registra un pago y lo asocia al tenant en la tabla tenant_payment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud de pago inválida"),
        @ApiResponse(responseCode = "404", description = "Tenant no encontrado")
    })
    @PostMapping("/register-payment")
    public ResponseEntity<GenericResponse> registerPayment(@RequestBody PagoDto pagoDto) {
        try {
            registroService.registrarPago(pagoDto);
            return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse(201, "SUCCESS", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResponse(404, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error during payment registration", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GenericResponse(400, "Invalid payment request body", null));
        }
    }

}
