package com.lealtixservice.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lealtixservice.service.QrCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio de generación de códigos QR usando ZXing
 */
@Service
@Slf4j
public class QrCodeServiceImpl implements QrCodeService {

    private static final int DEFAULT_QR_SIZE = 300;
    private static final String IMAGE_FORMAT = "PNG";

    @Override
    public byte[] generateQrCode(String content, int width, int height) throws IOException {
        log.debug("Generando código QR para contenido: {}", content);

        try {
            // Configurar opciones del QR
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            // Generar matriz del QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // Convertir a imagen
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convertir imagen a bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, IMAGE_FORMAT, byteArrayOutputStream);
            byte[] qrBytes = byteArrayOutputStream.toByteArray();

            log.info("Código QR generado exitosamente. Tamaño: {} bytes", qrBytes.length);
            return qrBytes;

        } catch (WriterException e) {
            log.error("Error al generar código QR: {}", e.getMessage(), e);
            throw new IOException("Error al generar código QR: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateQrCode(String content) throws IOException {
        return generateQrCode(content, DEFAULT_QR_SIZE, DEFAULT_QR_SIZE);
    }

    @Override
    public String generateQrCodeBase64(String content, int width, int height) throws IOException {
        byte[] qrBytes = generateQrCode(content, width, height);
        String base64 = Base64.getEncoder().encodeToString(qrBytes);
        log.debug("Código QR convertido a Base64. Longitud: {}", base64.length());
        return base64;
    }

    @Override
    public String generateQrCodeBase64(String content) throws IOException {
        return generateQrCodeBase64(content, DEFAULT_QR_SIZE, DEFAULT_QR_SIZE);
    }
}

