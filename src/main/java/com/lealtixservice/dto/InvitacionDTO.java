package com.lealtixservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class InvitacionDTO {
    @NotNull
    private Long preRegistroId;
    private String email; // Opcional
}

