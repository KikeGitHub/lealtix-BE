package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.dto.ProductPriceRequest;
import com.lealtixservice.dto.CheckoutSessionRequest;
import com.stripe.model.Product;
import com.stripe.model.Price;
import com.lealtixservice.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Stripe", description = "Operaciones relacionadas con Stripe: productos, precios y sesiones de pago")
@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @Operation(summary = "Crear producto y precio en Stripe", description = "Crea un producto y un precio en Stripe usando los datos proporcionados.")
    @PostMapping("/product-with-price")
    public ResponseEntity<?> createProductWithPrice(@RequestBody ProductPriceRequest request) {
        try {
            Product product = stripeService.createProductWithPrice(request);
            Price price = stripeService.getLastCreatedPrice();
            return ResponseEntity.ok().body("Product ID: " + product.getId() + ", Price ID: " + price.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Crear sesión de checkout en Stripe", description = "Crea una sesión de checkout en Stripe para el pago.")
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutSessionRequest request) {
        try {
            Map<String, Object> response = stripeService.createCheckoutSession(request.getPriceId(), request.getTenantId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener sesión de checkout cancelada", description = "Obtiene los detalles de una sesión de checkout cancelada de Stripe por su ID.")
    @GetMapping("/checkout-cancel/{sessionId}")
    public ResponseEntity<GenericResponse> getCheckoutCancel(@PathVariable String sessionId) {
        try {
            PagoDto pagoDto = stripeService.getCheckoutSessionCancel(sessionId);
            if (pagoDto != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new GenericResponse(200, "SUCCESS", pagoDto));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse(404, "NOT FOUND", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse(500, "Internal server error", null));
        }
    }
}
