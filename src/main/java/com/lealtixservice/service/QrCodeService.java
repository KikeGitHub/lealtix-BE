package com.lealtixservice.service;

import java.io.IOException;

/**
 * Servicio para la generación de códigos QR
 */
public interface QrCodeService {

    /**
     * Genera un código QR a partir de un texto/URL
     *
     * @param content El contenido a codificar en el QR (ejemplo: URL de redención)
     * @param width Ancho de la imagen en píxeles
     * @param height Alto de la imagen en píxeles
     * @return El QR como array de bytes en formato PNG
     * @throws IOException Si ocurre un error al generar el QR
     */
    byte[] generateQrCode(String content, int width, int height) throws IOException;

    /**
     * Genera un código QR con dimensiones por defecto (300x300)
     *
     * @param content El contenido a codificar en el QR
     * @return El QR como array de bytes en formato PNG
     * @throws IOException Si ocurre un error al generar el QR
     */
    byte[] generateQrCode(String content) throws IOException;

    /**
     * Genera un código QR y lo retorna codificado en Base64
     *
     * @param content El contenido a codificar en el QR
     * @param width Ancho de la imagen en píxeles
     * @param height Alto de la imagen en píxeles
     * @return El QR codificado en Base64
     * @throws IOException Si ocurre un error al generar el QR
     */
    String generateQrCodeBase64(String content, int width, int height) throws IOException;

    /**
     * Genera un código QR con dimensiones por defecto y lo retorna en Base64
     *
     * @param content El contenido a codificar en el QR
     * @return El QR codificado en Base64
     * @throws IOException Si ocurre un error al generar el QR
     */
    String generateQrCodeBase64(String content) throws IOException;
}

