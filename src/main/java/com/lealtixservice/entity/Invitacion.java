package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitacion", uniqueConstraints = @UniqueConstraint(columnNames = "token"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_registro_id", nullable = false)
    private PreRegistro preRegistro;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(nullable = false)
    private String estado;

    private LocalDateTime fechaAceptado;
}

