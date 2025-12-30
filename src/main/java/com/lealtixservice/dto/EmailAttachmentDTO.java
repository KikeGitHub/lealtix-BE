package com.lealtixservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para adjuntos de email (inline o attachment)
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO para adjuntos en emails")
public class EmailAttachmentDTO {

    @Schema(description = "Contenido del archivo codificado en Base64", example = "iVBORw0KGgoAAAANSUhEUgAA...")
    private String content;

    @Schema(description = "Tipo MIME del archivo", example = "image/png")
    private String type;

    @Schema(description = "Nombre del archivo", example = "coupon-qr.png")
    private String filename;

    @Schema(description = "Disposición del adjunto (inline o attachment)", example = "inline")
    private String disposition;

    @Schema(description = "Content ID para referencias inline en HTML", example = "couponQr")
    private String contentId;
}

