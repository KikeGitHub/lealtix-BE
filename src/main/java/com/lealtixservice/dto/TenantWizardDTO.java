package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantWizardDTO {

    private AppUserDTO user;
    private TenantDTO tenant;
    private TenantConfigDTO tenantConfig;

}
