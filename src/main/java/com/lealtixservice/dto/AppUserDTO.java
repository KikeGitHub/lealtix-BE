package com.lealtixservice.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDTO {
    private Long id;
    private String fullName;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String email;
    private  String password;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

