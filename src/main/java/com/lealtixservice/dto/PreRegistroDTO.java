package com.lealtixservice.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class PreRegistroDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    @Email
    private String email;
}

