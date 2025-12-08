package com.lealtixservice.controller;

import com.lealtixservice.dto.*;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
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
            log.error("Error updating campaign", ex);
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            log.error("Error updating campaign", ex);
            return ResponseEntity.ok(new GenericResponse(400, ex.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating campaign", e);
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

    // ===== ENDPOINTS PARA BORRADORES =====

    @Operation(summary = "Crear campaña directamente (no borrador)")
    @PostMapping("/final")
    public ResponseEntity<GenericResponse> createFinal(@Valid @RequestBody CreateCampaignDto dto) {
        try {
            CampaignResponse response = campaignService.create(dto);
            return ResponseEntity.ok(new GenericResponse(201, "Campaña creada exitosamente", response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new GenericResponse(400, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Crear borrador de campaña")
    @PostMapping("/draft")
    public ResponseEntity<GenericResponse> createDraft(@Valid @RequestBody CreateCampaignDraftDto dto) {
        try {
            CampaignResponse response = campaignService.createDraft(dto);
            return ResponseEntity.ok(new GenericResponse(201, "Borrador creado exitosamente", response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new GenericResponse(400, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Actualizar borrador de campaña")
    @PutMapping("/draft/{id}")
    public ResponseEntity<GenericResponse> updateDraft(@PathVariable Long id, @Valid @RequestBody CreateCampaignDraftDto dto) {
        try {
            CampaignResponse response = campaignService.updateDraft(id, dto);
            return ResponseEntity.ok(new GenericResponse(200, "Borrador actualizado exitosamente", response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.ok(new GenericResponse(400, ex.getReason(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new GenericResponse(400, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Publicar borrador de campaña")
    @PostMapping("/draft/{id}/publish")
    public ResponseEntity<GenericResponse> publishDraft(@PathVariable Long id) {
        try {
            CampaignResponse response = campaignService.publishDraft(id);
            return ResponseEntity.ok(new GenericResponse(200, "Campaña publicada exitosamente", response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.ok(new GenericResponse(400, ex.getReason(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Obtener borradores por businessId")
    @GetMapping("/business/{businessId}/drafts")
    public ResponseEntity<GenericResponseProd> getDraftsByBusiness(@PathVariable Long businessId) {
        try {
            List<CampaignResponse> drafts = campaignService.getDraftsByBusiness(businessId);
            return ResponseEntity.ok(new GenericResponseProd(200, "Borradores obtenidos exitosamente", drafts, drafts.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponseProd(500, "Error interno", null, 0));
        }
    }

    @Operation(summary = "Obtener campañas activas por businessId")
    @GetMapping("/business/{businessId}/active")
    public ResponseEntity<GenericResponseProd> getActiveCampaigns(@PathVariable Long businessId) {
        try {
            List<CampaignResponse> campaigns = campaignService.getActiveCampaigns(businessId);
            return ResponseEntity.ok(new GenericResponseProd(200, "Campañas activas obtenidas exitosamente", campaigns, campaigns.size()));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponseProd(500, "Error interno", null, 0));
        }
    }
}

