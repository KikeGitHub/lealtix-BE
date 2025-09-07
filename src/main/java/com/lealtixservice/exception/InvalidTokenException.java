package com.lealtixservice.exception;

/**
 * Excepción para token inválido o no encontrado.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}

