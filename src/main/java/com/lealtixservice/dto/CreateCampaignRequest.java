package com.lealtixservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignRequest {
    private Long templateId; // opcional
    private Long businessId;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String promoType;
    private String promoValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private String callToAction;
    private List<String> channels;
    private List<String> segmentation; // Cambiado a lista para aceptar payload del frontend
    private Boolean isAutomatic;
}

