package com.lealtixservice.enums;

public enum PromoType {
    DISCOUNT,
    AMOUNT,
    BOGO,
    FREE_ITEM,
    CUSTOM;

    public String getValue() {
        return name();
    }
}

