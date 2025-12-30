package com.lealtixservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear cupones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponDTO {

    @NotNull(message = "El ID de la campaña es obligatorio")
    private Long campaignId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;

    private LocalDateTime expiresAt; // Opcional, si no se envía se toma de la campaña
}

