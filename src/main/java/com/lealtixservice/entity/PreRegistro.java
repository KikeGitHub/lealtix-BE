package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pre_registro", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegistro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String status;

    private String description;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedDate = LocalDateTime.now();

}
