package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantMenuProductDTO {
    private Long id;
    private  Long categoryId;
    private String categoryName;
    private Long tenantId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
}
