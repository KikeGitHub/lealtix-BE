package com.lealtixservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateTokenResponse {
    private boolean ok;
    private String email;
    private  String message;
}

