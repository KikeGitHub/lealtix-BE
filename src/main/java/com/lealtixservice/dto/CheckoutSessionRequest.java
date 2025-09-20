package com.lealtixservice.dto;

import lombok.Data;

@Data
public class CheckoutSessionRequest {
    private String priceId;
    private String tenantId;
}
