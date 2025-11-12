package com.lealtixservice.dto;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PreRegistroDTO {

    private String nombre;
    private String email;
}

