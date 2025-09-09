package com.lealtixservice.controller;

import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.service.Emailservice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private Emailservice emailService;


    @Operation(summary = "Envía un correo electrónico usando el EmailService")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error al enviar el correo", content = @Content)
    })
    @PostMapping("/send")
    public ResponseEntity<GenericResponse> sendEmailWithTemplate(
            @RequestBody EmailDTO emailDTO) {
        try {
            emailService.sendEmailWithTemplate(emailDTO);
            return ResponseEntity.ok(new GenericResponse("200", "SUCCESS", null));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new GenericResponse("500", "Error al enviar el correo: " + e.getMessage(), null));
        }
    }
}
