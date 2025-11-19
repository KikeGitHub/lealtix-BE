package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_menu_category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantMenuCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Builder.Default
    private boolean isActive = true;

    @Column
    private Integer displayOrder;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

   @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}