package com.lealtixservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResultDTO {
    private Long id;
    private Long campaignId;
    private Integer views;
    private Integer clicks;
    private Integer redemptions;
    private LocalDateTime lastViewAt;
    private LocalDateTime lastRedemptionAt;
}

