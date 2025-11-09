package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.LoginRequest;
import com.lealtixservice.dto.JwtResponse;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.util.JwtUtil;
import com.lealtixservice.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Operaciones de login y autenticación JWT")
public class LoginController {
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Login de usuario", description = "Autentica al usuario y retorna un JWT si las credenciales son correctas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna JWT y datos de usuario"),
        @ApiResponse(responseCode = "401", description = "Usuario no encontrado o contraseña incorrecta")
    })
    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@RequestBody LoginRequest loginRequest) {
        AppUser user = appUserRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse(401, "Usuario no encontrado", null));
        }
        String decodedPassword = new String(Base64.getDecoder().decode(user.getPasswordHash()));
        if (!decodedPassword.equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse(401, "Contraseña incorrecta", null));
        }
        String token = jwtUtil.generateToken(user.getEmail());
        JwtResponse response = new JwtResponse(token, user.getEmail(), user.getId());
        return ResponseEntity.ok(new GenericResponse(200, "Login exitoso", response));
    }
}
