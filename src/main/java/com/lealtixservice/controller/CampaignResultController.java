package com.lealtixservice.controller;

import com.lealtixservice.dto.CampaignResultDTO;
import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.service.CampaignResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaign-results")
@Tag(name = "CampaignResult", description = "Métricas de campañas")
@RequiredArgsConstructor
public class CampaignResultController {

    private final CampaignResultService resultService;

    @Operation(summary = "Obtener métricas por campaignId")
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<GenericResponse> getByCampaign(@PathVariable Long campaignId) {
        try {
            CampaignResultDTO dto = resultService.findByCampaignId(campaignId);
            return ResponseEntity.ok(new GenericResponse(200, "Métricas obtenidas", dto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Incrementar vistas")
    @PostMapping("/campaign/{campaignId}/views")
    public ResponseEntity<GenericResponse> incrementViews(@PathVariable Long campaignId) {
        try {
            CampaignResultDTO dto = resultService.incrementViews(campaignId);
            return ResponseEntity.ok(new GenericResponse(200, "Vistas incrementadas", dto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Incrementar clicks")
    @PostMapping("/campaign/{campaignId}/clicks")
    public ResponseEntity<GenericResponse> incrementClicks(@PathVariable Long campaignId) {
        try {
            CampaignResultDTO dto = resultService.incrementClicks(campaignId);
            return ResponseEntity.ok(new GenericResponse(200, "Clicks incrementados", dto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }

    @Operation(summary = "Incrementar redemptions")
    @PostMapping("/campaign/{campaignId}/redemptions")
    public ResponseEntity<GenericResponse> incrementRedemptions(@PathVariable Long campaignId) {
        try {
            CampaignResultDTO dto = resultService.incrementRedemptions(campaignId);
            return ResponseEntity.ok(new GenericResponse(200, "Redemptions incrementadas", dto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.ok(new GenericResponse(500, "Error interno", null));
        }
    }
}

