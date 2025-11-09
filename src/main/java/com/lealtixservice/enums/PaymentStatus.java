package com.lealtixservice.enums;

public enum PaymentStatus {
    INITIATED,
    SUCCESS,
    FAILED,
    CANCELED;

    public String getStatus() {
        return this.name();
    }
}
