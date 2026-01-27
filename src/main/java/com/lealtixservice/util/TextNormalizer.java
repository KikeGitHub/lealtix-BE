package com.lealtixservice.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Utilidad para normalizar texto de manera consistente.
 * Aplica transformaciones para facilitar comparaciones case-insensitive
 * y tolerantes a variaciones ortográficas.
 */
public final class TextNormalizer {

    private TextNormalizer() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Normaliza un texto eliminando acentos, convirtiendo a minúsculas
     * y aplicando singularización simple.
     *
     * @param text texto a normalizar
     * @return texto normalizado o cadena vacía si el input es null
     */
    public static String normalize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String trimmed = text.trim().toLowerCase(Locale.ROOT);

        // Eliminar acentos y diacríticos
        String withoutAccents = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Singularización simple: eliminar 's' final si existe
        return singularize(withoutAccents);
    }

    /**
     * Aplica singularización simple eliminando 's' final.
     * Evita singularizar palabras de una sola letra.
     *
     * @param text texto a singularizar
     * @return texto singularizado
     */
    private static String singularize(String text) {
        if (text.endsWith("s") && text.length() > 2) {
            return text.substring(0, text.length() - 1);
        }
        return text;
    }

    /**
     * Verifica si dos textos son equivalentes después de normalización.
     *
     * @param text1 primer texto
     * @param text2 segundo texto
     * @return true si los textos normalizados son iguales
     */
    public static boolean areEquivalent(String text1, String text2) {
        return normalize(text1).equals(normalize(text2));
    }
}
