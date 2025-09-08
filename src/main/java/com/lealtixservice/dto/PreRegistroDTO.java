package com.lealtixservice.dto;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PreRegistroDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    @Email
    private String email;
}

