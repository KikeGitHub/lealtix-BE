package com.lealtixservice.service;

import com.lealtixservice.dto.PagoDto;
import com.stripe.model.Event;

public interface StripeWebhookService {
    PagoDto handleCheckoutSessionCompleted(Event event);
    void handlePaymentIntentSucceeded(Event event);
    void handlePaymentIntentFailed(Event event);
    void handleChargeFailed(Event event);
}
