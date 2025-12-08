package com.lealtixservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignRequest {
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String promoType;
    private String promoValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String callToAction;
    private List<String> channels;
    private List<String> segmentation;
    private Boolean isAutomatic;
}

