package com.lealtixservice.dto;

import lombok.Data;

@Data
public class ProductPriceRequest {
    private String name;
    private String description;
    private String currency;
    private Long amount;
    private String interval; // e.g. "month", "year"
}
