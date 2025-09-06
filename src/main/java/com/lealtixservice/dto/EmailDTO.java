package com.lealtixservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para el envío de correos electrónicos con plantilla y datos dinámicos")
public class EmailDTO {

    @Schema(description = "Correo destinatario", example = "usuario@dominio.com")
    private String to;

    @Schema(description = "Asunto del correo", example = "Bienvenido")
    private String subject;

    @Schema(description = "ID de la plantilla de SendGrid", example = "d-1234567890abcdef")
    private String templateId;

    @Schema(description = "Datos dinámicos para la plantilla", example = "{ 'nombre': 'Juan', 'codigo': '1234' }")
    private Map<String, Object> dynamicData;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String, Object> getDynamicData() {
        return dynamicData;
    }

    public void setDynamicData(Map<String, Object> dynamicData) {
        this.dynamicData = dynamicData;
    }
}
