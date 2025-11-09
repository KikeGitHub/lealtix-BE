package com.lealtixservice.controller;

import com.lealtixservice.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jwt")
@Tag(name = "JWT Controller", description = "Operaciones para generación y validación de JWT")
public class JWTController {

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "Genera un token JWT", description = "Genera un token JWT usando tenantId y email.")
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateToken(@RequestParam Long tenantId, @RequestParam String email) {
        String token = tokenService.generateToken(tenantId, email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Valida un token JWT", description = "Valida un token JWT y retorna los claims si es válido.")
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            Jws<Claims> claims = tokenService.validateToken(token);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("claims", claims.getBody());
            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}

