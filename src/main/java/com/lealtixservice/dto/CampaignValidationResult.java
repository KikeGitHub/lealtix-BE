package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignValidationResult {
    private Long campaignId;
    private boolean configComplete;
    private List<String> missingItems;
    // Severity para indicar nivel visual: "OK" | "ACTION_REQUIRED"
    private String severity;
}
