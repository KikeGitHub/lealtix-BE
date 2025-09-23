package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenant_config")
public class TenantConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private String tipoNegocio;

    @Column(length = 15)
    private String since;

    private String imgLogo;

    @Column(length = 500)
    private String story;

    @Column(length = 500)
    private String vision;

    @ElementCollection
    @CollectionTable(name = "tenant_config_list_vision", joinColumns = @JoinColumn(name = "tenant_config_id"))
    @Column(name = "vision_component", length = 200)
    private List<String> listVision;

    private String bussinesEmail;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;
    private String tiktok;

    @ElementCollection
    @CollectionTable(name = "tenant_config_schedules", joinColumns = @JoinColumn(name = "tenant_config_id"))
    private List<Schedule> schedules;

    @Builder.Default
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Schedule {
        private String day;
        private String hours;
    }
}
