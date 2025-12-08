package com.lealtixservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignDraftDto {

    @NotNull(message = "El ID del negocio es obligatorio")
    private Long businessId;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 255, message = "El título debe tener entre 3 y 255 caracteres")
    private String title;

    @Size(max = 200, message = "El subtítulo no puede superar los 200 caracteres")
    private String subtitle;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    private String description;

    @Size(max = 500, message = "La URL de imagen no puede superar los 500 caracteres")
    private String imageUrl;

    private String promoType;

    @Size(max = 200, message = "El valor promocional no puede superar los 200 caracteres")
    private String promoValue;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(max = 200, message = "El call to action no puede superar los 200 caracteres")
    private String callToAction;

    private List<String> channels;

    // Cambiado a lista para aceptar el payload del frontend
    private List<String> segmentation;

    private Boolean isAutomatic;

    private Long templateId;
}
