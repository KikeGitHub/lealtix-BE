package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenant_payment")
public class TenantPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String stripePaymentMethodId;
    private String plan;
    private String status; // pending, active, canceled, trial
    private java.time.LocalDateTime startDate;
    private java.time.LocalDateTime endDate;
    private String description;
    private String name;
    private String UIDTenant;
    @Builder.Default
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
