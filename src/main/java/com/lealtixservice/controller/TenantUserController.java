package com.lealtixservice.controller;

import com.lealtixservice.dto.AppUserDTO;
import com.lealtixservice.dto.TenantDTO;
import com.lealtixservice.dto.TenantUserDTO;
import com.lealtixservice.dto.TenantUserIdDTO;
import com.lealtixservice.entity.TenantUser;
import com.lealtixservice.entity.TenantUserId;
import com.lealtixservice.service.TenantUserService;
import com.lealtixservice.util.TenantUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tenant-user")
@Tag(name = "TenantUser Controller", description = "Operaciones CRUD para TenantUser")
public class TenantUserController {

    @Autowired
    private TenantUserService tenantUserService;

    @Operation(summary = "Buscar TenantUser por ID compuesto")
    @GetMapping("/findById")
    public ResponseEntity<TenantUserDTO> findById(@RequestBody TenantUserIdDTO idDto) {
        TenantUserId id = new TenantUserId(idDto.getTenantId(), idDto.getUserId(), idDto.getRoleId());
        Optional<TenantUser> tenantUser = tenantUserService.findById(id);
        return tenantUser.map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar TenantUser por userId")
    @GetMapping("/findByUserId/{userId}")
    public ResponseEntity<TenantUserDTO> findByUserId(@PathVariable Long userId) {
        Optional<TenantUser> tenantUser = tenantUserService.findByUserId(userId);
        return tenantUser.map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar TenantUser por email")
    @GetMapping("/findByEmail/{email}")
    public ResponseEntity<TenantUserDTO> findByUserId(@PathVariable String email) {
        Optional<TenantUser> tenantUser = tenantUserService.findByEmail(email);
        return tenantUser.map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Guardar o actualizar TenantUser")
    @PostMapping("/save")
    public ResponseEntity<TenantUserDTO> save(@RequestBody TenantUserDTO dto) {
        TenantUser entity = toEntity(dto);
        TenantUser saved = tenantUserService.save(entity);
        return ResponseEntity.ok(toDTO(saved));
    }

    @Operation(summary = "Eliminar TenantUser por ID compuesto")
    @DeleteMapping("/deleteById")
    public ResponseEntity<Void> deleteById(@RequestBody TenantUserIdDTO idDto) {
        TenantUserId id = new TenantUserId(idDto.getTenantId(), idDto.getUserId(), idDto.getRoleId());
        tenantUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos de conversión DTO <-> Entity
    private TenantUserDTO toDTO(TenantUser entity) {
        return TenantUserMapper.toDTO(entity);
    }

    private TenantUser toEntity(TenantUserDTO dto) {
        return TenantUserMapper.toEntity(dto);
    }
}
