package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.GenericResponseProd;
import com.lealtixservice.dto.PromotionRewardDTO;
import com.lealtixservice.dto.PromotionRewardResponse;
import com.lealtixservice.exception.BusinessRuleException;
import com.lealtixservice.exception.ResourceNotFoundException;
import com.lealtixservice.service.PromotionRewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/promotion-rewards")
@Tag(name = "Promotion Reward", description = "Gestión de recompensas de promociones (PromotionReward)")
@RequiredArgsConstructor
public class PromotionRewardController {

    private final PromotionRewardService promotionRewardService;

    @Operation(
            summary = "Obtener reward por ID",
            description = "Busca un PromotionReward específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reward encontrado"),
            @ApiResponse(responseCode = "404", description = "Reward no encontrado")
    })
    @GetMapping("/{rewardId}")
    public ResponseEntity<GenericResponse> findById(
            @Parameter(description = "ID del PromotionReward", required = true)
            @PathVariable Long rewardId) {
        try {
            PromotionRewardResponse response = promotionRewardService.findById(rewardId);
            return ResponseEntity.ok(new GenericResponse(200, "Reward obtenido exitosamente", response));
        } catch (ResourceNotFoundException ex) {
            log.error("Reward no encontrado: {}", ex.getMessage());
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            log.error("Error obteniendo reward", e);
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(
            summary = "Obtener reward por campaña",
            description = "Busca el PromotionReward asociado a una campaña específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reward encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe reward para esta campaña")
    })
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<GenericResponse> findByCampaignId(
            @Parameter(description = "ID de la campaña", required = true)
            @PathVariable Long campaignId) {
        try {
            PromotionRewardResponse response = promotionRewardService.findByCampaignId(campaignId);
            return ResponseEntity.ok(new GenericResponse(200, "Reward obtenido exitosamente", response));
        } catch (ResourceNotFoundException ex) {
            log.error("Reward no encontrado para campaña: {}", ex.getMessage());
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            log.error("Error obteniendo reward por campaña", e);
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(
            summary = "Listar todos los rewards",
            description = "Obtiene la lista completa de PromotionRewards (útil para administradores)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<GenericResponseProd> findAll() {
        try {
            List<PromotionRewardResponse> list = promotionRewardService.findAll();
            return ResponseEntity.ok(new GenericResponseProd(200, "Rewards obtenidos exitosamente", list, list.size()));
        } catch (Exception e) {
            log.error("Error listando rewards", e);
            return ResponseEntity.ok(new GenericResponseProd(500, "Error interno del servidor", null, 0));
        }
    }

    @Operation(
            summary = "Actualizar reward existente",
            description = "Actualiza los datos de un PromotionReward. Valida que los campos requeridos estén presentes según el tipo de reward."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reward actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reward no encontrado"),
            @ApiResponse(responseCode = "422", description = "Validación de negocio fallida")
    })
    @PutMapping("/{rewardId}")
    public ResponseEntity<GenericResponse> update(
            @Parameter(description = "ID del PromotionReward a actualizar", required = true)
            @PathVariable Long rewardId,
            @Parameter(description = "Datos actualizados del reward", required = true)
            @Valid @RequestBody PromotionRewardDTO dto) {
        try {
            PromotionRewardResponse response = promotionRewardService.update(rewardId, dto);
            return ResponseEntity.ok(new GenericResponse(200, "Reward actualizado exitosamente", response));
        } catch (ResourceNotFoundException ex) {
            log.error("Reward no encontrado: {}", ex.getMessage());
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (BusinessRuleException ex) {
            log.error("Error de validación: {}", ex.getMessage());
            return ResponseEntity.ok(new GenericResponse(422, ex.getMessage(), null));
        } catch (Exception e) {
            log.error("Error actualizando reward", e);
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(
            summary = "Eliminar reward",
            description = "Elimina un PromotionReward (desvincula de la campaña). PRECAUCIÓN: Esta acción es irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reward eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reward no encontrado")
    })
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<GenericResponse> delete(
            @Parameter(description = "ID del PromotionReward a eliminar", required = true)
            @PathVariable Long rewardId) {
        try {
            promotionRewardService.delete(rewardId);
            return ResponseEntity.ok(new GenericResponse(200, "Reward eliminado exitosamente", null));
        } catch (ResourceNotFoundException ex) {
            log.error("Reward no encontrado: {}", ex.getMessage());
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (Exception e) {
            log.error("Error eliminando reward", e);
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(
            summary = "Incrementar uso del reward",
            description = "Incrementa el contador de uso del reward. Se usa cuando un cliente aplica el descuento/beneficio."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uso incrementado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reward no encontrado"),
            @ApiResponse(responseCode = "422", description = "Límite de uso alcanzado")
    })
    @PostMapping("/{rewardId}/increment-usage")
    public ResponseEntity<GenericResponse> incrementUsage(
            @Parameter(description = "ID del PromotionReward", required = true)
            @PathVariable Long rewardId) {
        try {
            promotionRewardService.incrementUsage(rewardId);
            return ResponseEntity.ok(new GenericResponse(200, "Uso incrementado exitosamente", null));
        } catch (ResourceNotFoundException ex) {
            log.error("Reward no encontrado: {}", ex.getMessage());
            return ResponseEntity.ok(new GenericResponse(404, ex.getMessage(), null));
        } catch (BusinessRuleException ex) {
            log.error("Límite alcanzado: {}", ex.getMessage());
            return ResponseEntity.ok(new GenericResponse(422, ex.getMessage(), null));
        } catch (Exception e) {
            log.error("Error incrementando uso", e);
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    @Operation(
            summary = "Verificar límite de uso",
            description = "Verifica si el reward ha alcanzado su límite de uso configurado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación exitosa")
    })
    @GetMapping("/{rewardId}/usage-limit-reached")
    public ResponseEntity<GenericResponse> isUsageLimitReached(
            @Parameter(description = "ID del PromotionReward", required = true)
            @PathVariable Long rewardId) {
        try {
            boolean limitReached = promotionRewardService.isUsageLimitReached(rewardId);
            UsageLimitResponse response = UsageLimitResponse.builder()
                    .rewardId(rewardId)
                    .limitReached(limitReached)
                    .message(limitReached ? "El reward ha alcanzado su límite de uso" :
                            "El reward aún puede ser utilizado")
                    .build();
            return ResponseEntity.ok(new GenericResponse(200, "Verificación completada", response));
        } catch (Exception e) {
            log.error("Error verificando límite de uso", e);
            return ResponseEntity.ok(new GenericResponse(500, "Error interno del servidor", null));
        }
    }

    // DTO interno para respuesta de límite de uso
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UsageLimitResponse {
        private Long rewardId;
        private Boolean limitReached;
        private String message;
    }
}

