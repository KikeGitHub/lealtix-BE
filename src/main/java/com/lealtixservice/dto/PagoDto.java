package com.lealtixservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PagoDto {
    private Long tenantId;
    private String plan;
    private String status;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String stripePaymentMethodId;
    private LocalDateTime startDate ;
    private LocalDateTime endDate;
}

