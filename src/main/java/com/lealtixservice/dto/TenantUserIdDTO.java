package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para TenantUserId (ID compuesto)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantUserIdDTO {
    private Long tenantId;
    private Long userId;
    private Long roleId;
}

