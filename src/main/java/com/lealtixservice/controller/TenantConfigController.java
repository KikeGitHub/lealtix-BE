package com.lealtixservice.controller;

import com.lealtixservice.dto.TenantConfigDTO;
import com.lealtixservice.service.TenantConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TenantConfig", description = "Operaciones para la configuración del tenant")
@RestController
@RequestMapping("/api/tenant-config")
@RequiredArgsConstructor
public class TenantConfigController {

    @Autowired
    private final TenantConfigService tenantConfigService;


    @Operation(summary = "Obtener configuraciones por tenant", description = "Devuelve todas las configuraciones asociadas a un tenant por su ID.")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<TenantConfigDTO>> getTenantConfigsByTenant(@PathVariable Long tenantId) {
        List<TenantConfigDTO> list = tenantConfigService.getTenantConfigsByTenantId(tenantId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Guardar configuración de tenant", description = "Guarda una nueva configuración para un tenant.")
    @PostMapping
    public ResponseEntity<TenantConfigDTO> saveTenantConfig(@RequestBody TenantConfigDTO dto) {
        TenantConfigDTO saved = tenantConfigService.saveTenantConfig(dto);
        if (saved == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(saved);
    }
}
