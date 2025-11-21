package com.lealtixservice.controller;

import com.lealtixservice.dto.*;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@Tag(name = "Campaign", description = "Operaciones sobre campañas")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @Operation(summary = "Crear campaña")
    @PostMapping
    public ResponseEntity<GenericResponse> create(@RequestBody CreateCampaignRequest request) {
        try {
            CampaignResponse response = campaignService.create(request);
            return ResponseEntity.ok(new GenericResponse(201, "Campaña creada", response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new GenericResponse(400, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Actualizar campaña")
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse> update(@PathVariable Long id, @RequestBody UpdateCampaignRequest request) {
        try {
            CampaignResponse response = campaignService.update(id, request);
            return ResponseEntity.ok(new GenericResponse(200, "Campaña actualizada", response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new GenericResponse(400, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Obtener campaña por id")
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse> findById(@PathVariable Long id) {
        try {
            CampaignResponse response = campaignService.findById(id);
            return ResponseEntity.ok(new GenericResponse(200, "Campaña obtenida", response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Listar campañas por businessId")
    @GetMapping("/business/{businessId}")
    public ResponseEntity<GenericResponseProd> findByBusiness(@PathVariable Long businessId) {
        try {
            List<CampaignResponse> list = campaignService.findByBusinessId(businessId);
            return ResponseEntity.ok(new GenericResponseProd(200, "Campañas obtenidas", list, list.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponseProd(500, "Error interno", null, 0));
        }
    }

    @Operation(summary = "Eliminar campaña")
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable Long id) {
        try {
            campaignService.delete(id);
            return ResponseEntity.ok(new GenericResponse(200, "Campaña eliminada", null));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }
}

