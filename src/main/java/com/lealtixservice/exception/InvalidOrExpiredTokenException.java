package com.lealtixservice.exception;

public class InvalidOrExpiredTokenException extends RuntimeException {
    public InvalidOrExpiredTokenException(String message) {
        super(message);
    }
}

