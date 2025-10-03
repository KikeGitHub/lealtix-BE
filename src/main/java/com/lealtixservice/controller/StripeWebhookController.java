package com.lealtixservice.controller;

import com.lealtixservice.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;

@Tag(name = "Stripe Webhook", description = "Endpoints para recibir y procesar webhooks de Stripe")
@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private StripeWebhookService stripeWebhookService;

    @Operation(summary = "Recibe eventos webhook de Stripe", description = "Recibe y procesa eventos enviados por Stripe. Verifica la firma y maneja los eventos principales.")
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request,
                                                      @RequestHeader(name = "Stripe-Signature", required = false) String sigHeader) {
        String payload = getRawBody(request);
        if (sigHeader == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta Stripe-Signature");
        }
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                stripeWebhookService.handleCheckoutSessionCompleted(event);
                break;
            case "payment_intent.succeeded":
                stripeWebhookService.handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                stripeWebhookService.handlePaymentIntentFailed(event);
                break;
            default:
                // Otros eventos pueden ser manejados aquí si es necesario
                break;
        }
        return ResponseEntity.ok("Evento procesado");
    }

    private String getRawBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // Manejo de error de lectura
        }
        return sb.toString();
    }
}
