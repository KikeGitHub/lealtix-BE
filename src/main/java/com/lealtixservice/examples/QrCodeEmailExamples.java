package com.lealtixservice.examples;

import com.lealtixservice.dto.EmailAttachmentDTO;
import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.service.Emailservice;
import com.lealtixservice.service.QrCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejemplos de uso del QrCodeService integrado con emails
 *
 * NOTA: Esta clase es solo para referencia y ejemplos.
 * No se ejecuta automáticamente.
 */
@Component
@Slf4j
public class QrCodeEmailExamples {

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private Emailservice emailService;

    /**
     * Ejemplo 1: Enviar email de bienvenida con QR de cupón
     */
    public void sendWelcomeEmailWithCouponQr(String customerEmail, String customerName,
                                              String couponCode, String tenantName) throws IOException {

        log.info("Enviando email de bienvenida a {} con cupón {}", customerEmail, couponCode);

        // 1. Generar URL de redención
        String redeemUrl = "https://tudominio.com/redeem?code=" + couponCode;

        // 2. Generar QR code
        String qrBase64 = qrCodeService.generateQrCodeBase64(redeemUrl);

        // 3. Crear attachment inline
        EmailAttachmentDTO qrAttachment = EmailAttachmentDTO.builder()
                .content(qrBase64)
                .type("image/png")
                .filename("coupon-qr.png")
                .disposition("inline")
                .contentId("couponQr")
                .build();

        // 4. Preparar lista de attachments
        List<EmailAttachmentDTO> attachments = new ArrayList<>();
        attachments.add(qrAttachment);

        // 5. Preparar datos dinámicos para la plantilla
        Map<String, Object> dynamicData = new HashMap<>();
        dynamicData.put("customerName", customerName);
        dynamicData.put("tenantName", tenantName);
        dynamicData.put("couponCode", couponCode);
        dynamicData.put("hasQr", true);

        // 6. Crear EmailDTO
        EmailDTO emailDTO = EmailDTO.builder()
                .to(customerEmail)
                .subject("¡Bienvenido a " + tenantName + "!")
                .templateId("d-tu-template-id")
                .dynamicData(dynamicData)
                .attachments(attachments)
                .build();

        // 7. Enviar email
        emailService.sendEmailWithTemplate(emailDTO);

        log.info("Email enviado exitosamente a {}", customerEmail);
    }

    /**
     * Ejemplo 2: Enviar email con múltiples QRs (por ejemplo, varios cupones)
     */
    public void sendEmailWithMultipleQrs(String customerEmail, List<String> couponCodes) throws IOException {

        log.info("Enviando email con {} cupones a {}", couponCodes.size(), customerEmail);

        List<EmailAttachmentDTO> attachments = new ArrayList<>();

        // Generar un QR para cada cupón
        for (int i = 0; i < couponCodes.size(); i++) {
            String couponCode = couponCodes.get(i);
            String redeemUrl = "https://tudominio.com/redeem?code=" + couponCode;
            String qrBase64 = qrCodeService.generateQrCodeBase64(redeemUrl);

            EmailAttachmentDTO qrAttachment = EmailAttachmentDTO.builder()
                    .content(qrBase64)
                    .type("image/png")
                    .filename("coupon-qr-" + i + ".png")
                    .disposition("inline")
                    .contentId("couponQr" + i)
                    .build();

            attachments.add(qrAttachment);
        }

        // Preparar datos dinámicos
        Map<String, Object> dynamicData = new HashMap<>();
        dynamicData.put("couponCount", couponCodes.size());
        dynamicData.put("coupons", couponCodes);

        // Crear y enviar email
        EmailDTO emailDTO = EmailDTO.builder()
                .to(customerEmail)
                .subject("Tus cupones están listos")
                .templateId("d-multi-coupon-template-id")
                .dynamicData(dynamicData)
                .attachments(attachments)
                .build();

        emailService.sendEmailWithTemplate(emailDTO);

        log.info("Email con múltiples cupones enviado a {}", customerEmail);
    }

    /**
     * Ejemplo 3: Generar QR para cualquier URL (no solo cupones)
     */
    public void sendEmailWithCustomQr(String customerEmail, String customUrl,
                                       String qrLabel) throws IOException {

        log.info("Generando QR para URL: {}", customUrl);

        // Generar QR con tamaño personalizado
        String qrBase64 = qrCodeService.generateQrCodeBase64(customUrl, 400, 400);

        EmailAttachmentDTO qrAttachment = EmailAttachmentDTO.builder()
                .content(qrBase64)
                .type("image/png")
                .filename("custom-qr.png")
                .disposition("inline")
                .contentId("customQr")
                .build();

        List<EmailAttachmentDTO> attachments = new ArrayList<>();
        attachments.add(qrAttachment);

        Map<String, Object> dynamicData = new HashMap<>();
        dynamicData.put("qrLabel", qrLabel);
        dynamicData.put("targetUrl", customUrl);

        EmailDTO emailDTO = EmailDTO.builder()
                .to(customerEmail)
                .subject("Tu código QR")
                .templateId("d-custom-qr-template-id")
                .dynamicData(dynamicData)
                .attachments(attachments)
                .build();

        emailService.sendEmailWithTemplate(emailDTO);

        log.info("Email con QR personalizado enviado a {}", customerEmail);
    }

    /**
     * Ejemplo 4: Generar solo el QR sin enviar email (para otros usos)
     */
    public byte[] generateQrForDownload(String url) throws IOException {
        log.info("Generando QR para descarga: {}", url);

        // Generar QR como bytes (útil para retornar en un endpoint REST)
        byte[] qrBytes = qrCodeService.generateQrCode(url, 500, 500);

        log.info("QR generado, tamaño: {} bytes", qrBytes.length);
        return qrBytes;
    }

    /**
     * Ejemplo 5: Validar que el QR sea escaneable antes de enviar
     */
    public void sendEmailWithValidatedQr(String customerEmail, String couponCode) throws IOException {

        String redeemUrl = "https://tudominio.com/redeem?code=" + couponCode;

        try {
            // Generar QR
            String qrBase64 = qrCodeService.generateQrCodeBase64(redeemUrl);

            // Validar que el Base64 no esté vacío
            if (qrBase64 == null || qrBase64.isEmpty()) {
                log.error("QR generado está vacío para cupón {}", couponCode);
                throw new IOException("QR code generation failed");
            }

            log.info("QR validado exitosamente para cupón {}", couponCode);

            // Crear y enviar email...
            EmailAttachmentDTO qrAttachment = EmailAttachmentDTO.builder()
                    .content(qrBase64)
                    .type("image/png")
                    .filename("coupon-qr.png")
                    .disposition("inline")
                    .contentId("couponQr")
                    .build();

            List<EmailAttachmentDTO> attachments = new ArrayList<>();
            attachments.add(qrAttachment);

            Map<String, Object> dynamicData = new HashMap<>();
            dynamicData.put("couponCode", couponCode);
            dynamicData.put("hasQr", true);

            EmailDTO emailDTO = EmailDTO.builder()
                    .to(customerEmail)
                    .subject("Tu cupón con QR")
                    .templateId("d-coupon-template-id")
                    .dynamicData(dynamicData)
                    .attachments(attachments)
                    .build();

            emailService.sendEmailWithTemplate(emailDTO);

        } catch (IOException e) {
            log.error("Error generando o enviando QR para cupón {}: {}", couponCode, e.getMessage());
            throw e;
        }
    }
}

