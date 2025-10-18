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
public class TenantMenuCategoryDTO {

    private Long id;
    private Long tenantId;
    private String name;
    private List<TenantMenuProductDTO> productsDTO;

}
