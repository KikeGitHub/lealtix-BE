package com.lealtixservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {

    private String email;
    private String nombreNegocio;
    private String slogan;
    private  String type;
    private Long tenantId;
    private String productName;
    private String base64File;
}
