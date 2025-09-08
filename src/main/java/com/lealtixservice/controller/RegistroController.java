package com.lealtixservice.controller;

import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.service.RegistroService;
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

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistroDto registroDto) {
        try {
            registroService.register(registroDto);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }catch (Exception e) {
            log.error("Error during registration", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Registro exitoso");
    }
}

