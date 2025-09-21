package com.lealtixservice.service;

import com.stripe.model.Event;
import org.springframework.stereotype.Service;

@Service
public class StripeWebhookServiceImpl implements StripeWebhookService {
    @Override
    public void handleCheckoutSessionCompleted(Event event) {
        // Lógica para procesar checkout.session.completed
    }

    @Override
    public void handlePaymentIntentSucceeded(Event event) {
        // Lógica para procesar payment_intent.succeeded
    }

    @Override
    public void handlePaymentIntentFailed(Event event) {
        // Lógica para procesar payment_intent.payment_failed
    }
}

