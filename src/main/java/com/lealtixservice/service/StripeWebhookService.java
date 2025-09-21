package com.lealtixservice.service;

import com.stripe.model.Event;

public interface StripeWebhookService {
    void handleCheckoutSessionCompleted(Event event);
    void handlePaymentIntentSucceeded(Event event);
    void handlePaymentIntentFailed(Event event);
}

