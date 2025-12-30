package com.lealtixservice.service.impl;

import com.lealtixservice.service.QrCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para QrCodeService
 * No usa @SpringBootTest para evitar cargar todo el contexto
 */
class QrCodeServiceImplTest {

    private QrCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        // Crear instancia directa del servicio sin Spring
        qrCodeService = new QrCodeServiceImpl();
    }

    @Test
    void testGenerateQrCode_WithDefaultSize() throws IOException {
        // Given
        String testUrl = "https://example.com/redeem?code=TEST123";

        // When
        byte[] qrCode = qrCodeService.generateQrCode(testUrl);

        // Then
        assertNotNull(qrCode, "QR code no debe ser null");
        assertTrue(qrCode.length > 0, "QR code debe tener contenido");
    }

    @Test
    void testGenerateQrCode_WithCustomSize() throws IOException {
        // Given
        String testUrl = "https://example.com/redeem?code=TEST123";
        int width = 400;
        int height = 400;

        // When
        byte[] qrCode = qrCodeService.generateQrCode(testUrl, width, height);

        // Then
        assertNotNull(qrCode, "QR code no debe ser null");
        assertTrue(qrCode.length > 0, "QR code debe tener contenido");
    }

    @Test
    void testGenerateQrCodeBase64_WithDefaultSize() throws IOException {
        // Given
        String testUrl = "https://example.com/redeem?code=TEST123";

        // When
        String qrBase64 = qrCodeService.generateQrCodeBase64(testUrl);

        // Then
        assertNotNull(qrBase64, "QR code Base64 no debe ser null");
        assertFalse(qrBase64.isEmpty(), "QR code Base64 no debe estar vacío");

        // Verificar que es Base64 válido
        assertDoesNotThrow(() -> Base64.getDecoder().decode(qrBase64),
            "Debe ser Base64 válido");
    }

    @Test
    void testGenerateQrCodeBase64_WithCustomSize() throws IOException {
        // Given
        String testUrl = "https://example.com/redeem?code=TEST123";
        int width = 250;
        int height = 250;

        // When
        String qrBase64 = qrCodeService.generateQrCodeBase64(testUrl, width, height);

        // Then
        assertNotNull(qrBase64, "QR code Base64 no debe ser null");
        assertFalse(qrBase64.isEmpty(), "QR code Base64 no debe estar vacío");

        // Verificar que es Base64 válido
        assertDoesNotThrow(() -> Base64.getDecoder().decode(qrBase64),
            "Debe ser Base64 válido");
    }

    @Test
    void testGenerateQrCode_WithLongUrl() throws IOException {
        // Given
        String longUrl = "https://example.com/redeem?code=VERYLONGCOUPONCODE123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ&tenant=mytenant&customer=123";

        // When
        String qrBase64 = qrCodeService.generateQrCodeBase64(longUrl);

        // Then
        assertNotNull(qrBase64, "QR code debe generarse incluso con URL larga");
        assertFalse(qrBase64.isEmpty(), "QR code no debe estar vacío");
    }

    @Test
    void testGenerateQrCode_WithSpecialCharacters() throws IOException {
        // Given
        String urlWithSpecialChars = "https://example.com/redeem?code=TEST-123&user=Juan%20Pérez";

        // When
        String qrBase64 = qrCodeService.generateQrCodeBase64(urlWithSpecialChars);

        // Then
        assertNotNull(qrBase64, "QR code debe generarse con caracteres especiales");
        assertFalse(qrBase64.isEmpty(), "QR code no debe estar vacío");
    }

    @Test
    void testGenerateQrCode_ConsistentOutput() throws IOException {
        // Given
        String testUrl = "https://example.com/redeem?code=CONSISTENT";

        // When
        String qr1 = qrCodeService.generateQrCodeBase64(testUrl);
        String qr2 = qrCodeService.generateQrCodeBase64(testUrl);

        // Then
        assertEquals(qr1, qr2, "El mismo contenido debe generar el mismo QR");
    }
}

