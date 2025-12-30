package com.lealtixservice.enums;

public enum CouponStatus {
    ACTIVE,      // Cupón activo y disponible para usar
    REDEEMED,    // Cupón ya canjeado
    EXPIRED,     // Cupón expirado
    CANCELLED;   // Cupón cancelado

    public String getValue() {
        return name();
    }
}

