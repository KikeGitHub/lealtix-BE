package com.lealtixservice.exception;

/**
 * Excepción personalizada para violaciones de reglas de negocio.
 * Se utiliza cuando se intenta realizar una operación que no cumple
 * con las reglas de negocio establecidas.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}

