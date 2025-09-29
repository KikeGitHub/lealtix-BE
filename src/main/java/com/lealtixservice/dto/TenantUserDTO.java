package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para TenantUser
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantUserDTO {
    private TenantUserIdDTO id;
    private TenantDTO tenant;
    private AppUserDTO user;
    private Long roleId;
}

