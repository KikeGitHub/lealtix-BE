package com.lealtixservice.enums;

public enum RewardType {
    PERCENT_DISCOUNT,    // Descuento porcentual (ej. 20% off)
    FIXED_AMOUNT,        // Descuento monto fijo (ej. $500 off)
    FREE_PRODUCT,        // Producto gratis
    BUY_X_GET_Y,         // Compra X lleva Y (ej. 2x1)
    CUSTOM;              // Reward personalizado

    public String getValue() {
        return name();
    }
}

