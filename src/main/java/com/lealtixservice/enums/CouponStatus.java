package com.lealtixservice.enums;

public enum CouponStatus {
    CREATED,     // Cupón creado pero no enviado
    SENT,        // Cupón enviado al cliente (email/SMS/etc)
    ACTIVE,      // Cupón activo y disponible para usar
    REDEEMED,    // Cupón ya canjeado
    EXPIRED,     // Cupón expirado
    CANCELLED;   // Cupón cancelado

    public String getValue() {
        return name();
    }
}

