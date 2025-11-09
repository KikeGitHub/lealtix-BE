package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_menu_product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantMenuProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private TenantMenuCategory category;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(length = 255)
    private String imgUrl;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Builder.Default
    private boolean isActive = true;

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


