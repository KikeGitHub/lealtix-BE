package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.TenantPayment;
import com.lealtixservice.service.StripeService;
import com.lealtixservice.service.TenantPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Tenant Payment", description = "Operaciones para pagos y gestión de tenant_payment")
@RestController
@RequestMapping("/api/tenant-payment")
@RequiredArgsConstructor
public class TenantPaymentController {

    private static final Logger log = LoggerFactory.getLogger(TenantPaymentController.class);
    @Autowired
    private TenantPaymentService tenantService;

    private final StripeService stripeService;


    @Operation(summary = "Crear intento de pago", description = "Crea un intento de pago y lo asocia al tenant en la tabla tenant_payment.")
    @PostMapping("/intent")
    public ResponseEntity<GenericResponse> intentPayment(@RequestBody PagoDto pagoDto, @RequestParam("status") String status) {
        try {
            TenantPayment response = tenantService.intentPayment(pagoDto, status);
            log.info("Descripcion : {}", response.getDescription());
            return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse(HttpStatus.OK.value(),response.getDescription(), response.toString()));
        } catch (Exception e) {
            log.error("Error processing payment intent: {}", e.getMessage());
            GenericResponse errorResponse = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody PagoDto pagoDto) {
        try {
            Map<String, Object> response = stripeService.createPaymentIntent(pagoDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
