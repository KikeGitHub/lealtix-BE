package com.lealtixservice.controller;

import com.lealtixservice.dto.AppUserDTO;
import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "AppUser", description = "Operaciones relacionadas con usuarios de la aplicación")
@RestController
@RequestMapping("/api/appusers")
public class AppUserController {

    @Autowired
    private  AppUserService appUserService;

   @Operation(summary = "Crear un nuevo usuario")
    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        return ResponseEntity.ok(appUserService.save(user));
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        Optional<AppUser> user = appUserService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping
    public List<AppUser> getAllUsers() {
        return appUserService.findAll();
    }

    @Operation(summary = "Eliminar usuario por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener usuario por token")
    @GetMapping("/validate/{token}")
    public ResponseEntity<GenericResponse> getTenantUserByToken(@PathVariable String token) {
        try{
            var resp = appUserService.getTenantUserByToken(token);
            if(resp == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse(404, "NOT FOUND", null));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse(200, "SUCCESS", resp));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(500, e.getMessage(), null));
        }
    }

    @Operation(summary = "Actualizar un usuario existente")
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse> updateUser(@PathVariable Long id, @RequestBody AppUserDTO user) {
        try {
           var resp = appUserService.updateUser(id, user);
           return ResponseEntity.status(HttpStatus.OK)
                   .body(new GenericResponse(200, "SUCCESS", resp));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(500, e.getMessage(), null));
        }
    }
}
