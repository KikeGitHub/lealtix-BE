package com.lealtixservice.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class PreRegistroDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    @Email
    private String email;
}

