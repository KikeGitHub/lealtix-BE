package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.Tenant;
import com.lealtixservice.entity.TenantPayment;
import com.lealtixservice.enums.PaymentStatus;
import com.lealtixservice.service.TenantPaymentService;
import com.lealtixservice.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tenant-payment")
@RequiredArgsConstructor
public class TenantPaymentController {

    private static final Logger log = LoggerFactory.getLogger(TenantPaymentController.class);
    @Autowired
    private TenantPaymentService tenantService;

    @PostMapping("/intent")
    public ResponseEntity<GenericResponse> intentPayment(@RequestBody PagoDto pagoDto, @RequestParam("status") String status) {
        try {
            TenantPayment response = tenantService.intentPayment(pagoDto, status);
            log.info("Descripcion : ", response.getDescription());
            return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse(HttpStatus.OK.value(),response.getDescription(), response.toString()));
        } catch (Exception e) {
            log.error("Error processing payment intent: {}", e.getMessage());
            GenericResponse errorResponse = new GenericResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
