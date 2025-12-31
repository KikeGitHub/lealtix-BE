package com.lealtixservice.dto;

import com.lealtixservice.enums.RedemptionChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar la redención de un cupón.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemCouponRequest {

    @NotBlank(message = "El campo 'redeemedBy' es requerido")
    private String redeemedBy; // Usuario/email que redime

    @NotNull(message = "El campo 'channel' es requerido")
    private RedemptionChannel channel; // Canal de redención

    private String location; // Punto de venta, ubicación

    private String metadata; // Información adicional en JSON

    private String ipAddress; // IP del cliente (opcional, puede ser capturado automáticamente)

    private String userAgent; // User agent (opcional)
}

