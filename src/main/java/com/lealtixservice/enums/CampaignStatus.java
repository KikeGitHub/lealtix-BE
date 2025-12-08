package com.lealtixservice.enums;

public enum CampaignStatus {
    DRAFT,
    ACTIVE,
    INACTIVE,
    SCHEDULED;

    public String getValue() {
        return name();
    }
}

