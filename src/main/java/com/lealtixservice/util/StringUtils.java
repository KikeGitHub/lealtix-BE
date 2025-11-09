package com.lealtixservice.util;

import java.util.Base64;

public class StringUtils {
    public static String createSlug(String nombreNegocio, Long id) {
        if (nombreNegocio != null && id != null) {
            String name = nombreNegocio.toLowerCase().replaceAll("[^a-z0-9]+", "-");
            String uniqueId = String.valueOf(id);
            return name + "-" + uniqueId;
        }
        return null;
    }

    public static String decryptBase64(String base64) {
        if (base64 == null || base64.isBlank()) return base64;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            // Si no es base64 válido, retorna el valor original
            return base64;
        }
    }
}
