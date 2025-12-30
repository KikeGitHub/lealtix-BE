package com.lealtixservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_customer")
@Getter
@Setter
@ToString(exclude = {"tenant"})
@EqualsAndHashCode(exclude = {"tenant"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    private Tenant tenant;

    @Column(name = "name", length = 150, nullable = false)
    @NotBlank
    private String name;

    @Column(name = "email", length = 150, nullable = false)
    @NotBlank
    @Email
    private String email;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone", length = 20)
    @Size(max = 20)
    private String phone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Consentimiento para recibir promociones. Por defecto true.
    @Column(name = "accepted_promotions", nullable = false)
    private boolean acceptedPromotions = true;

    // Fecha en la que el usuario aceptó recibir promociones
    @Column(name = "accepted_at")
    private LocalDate acceptedAt;

}
