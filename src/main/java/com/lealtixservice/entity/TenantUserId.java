package com.lealtixservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantUserId implements Serializable {
    private Long tenantId;
    private Long userId;
    private Long roleId;
}

