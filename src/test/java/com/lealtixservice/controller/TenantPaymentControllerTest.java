package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.entity.TenantPayment;
import com.lealtixservice.service.TenantPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TenantPaymentControllerTest {

    @Mock
    private TenantPaymentService tenantPaymentService;

    @InjectMocks
    private TenantPaymentController tenantPaymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIntentPaymentSuccess() {
        PagoDto pagoDto = new PagoDto();
        String status = "SUCCESS";
        TenantPayment tenantPayment = TenantPayment.builder().description("Pago exitoso").build();
        when(tenantPaymentService.intentPayment(any(PagoDto.class), anyString())).thenReturn(tenantPayment);

        ResponseEntity<GenericResponse> result = tenantPaymentController.intentPayment(pagoDto, status);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Pago exitoso", result.getBody().getMessage());
    }

    @Test
    void testIntentPaymentFailed() {
        PagoDto pagoDto = new PagoDto();
        String status = "FAILED";
        TenantPayment tenantPayment = TenantPayment.builder().description("Pago fallido").build();
        when(tenantPaymentService.intentPayment(any(PagoDto.class), anyString())).thenReturn(tenantPayment);

        ResponseEntity<GenericResponse> result = tenantPaymentController.intentPayment(pagoDto, status);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Pago fallido", result.getBody().getMessage());
    }

    @Test
    void testIntentPaymentCanceled() {
        PagoDto pagoDto = new PagoDto();
        String status = "CANCELED";
        TenantPayment tenantPayment = TenantPayment.builder().description("Pago cancelado").build();
        when(tenantPaymentService.intentPayment(any(PagoDto.class), anyString())).thenReturn(tenantPayment);

        ResponseEntity<GenericResponse> result = tenantPaymentController.intentPayment(pagoDto, status);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Pago cancelado", result.getBody().getMessage());
    }

    @Test
    void testIntentPaymentInitiated() {
        PagoDto pagoDto = new PagoDto();
        String status = "INITIATED";
        TenantPayment tenantPayment = TenantPayment.builder().description("Intento de pago iniciado").build();
        when(tenantPaymentService.intentPayment(any(PagoDto.class), anyString())).thenReturn(tenantPayment);

        ResponseEntity<GenericResponse> result = tenantPaymentController.intentPayment(pagoDto, status);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Intento de pago iniciado", result.getBody().getMessage());
    }

    @Test
    void testIntentPaymentInvalidStatus() {
        PagoDto pagoDto = new PagoDto();
        String status = "INVALID";
        TenantPayment tenantPayment = TenantPayment.builder().description("Estatus de pago inválido").build();
        when(tenantPaymentService.intentPayment(any(PagoDto.class), anyString())).thenReturn(tenantPayment);

        ResponseEntity<GenericResponse> result = tenantPaymentController.intentPayment(pagoDto, status);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Estatus de pago inválido", result.getBody().getMessage());
    }

    @Test
    void testIntentPaymentException() {
        PagoDto pagoDto = new PagoDto();
        String status = "SUCCESS";
        when(tenantPaymentService.intentPayment(any(PagoDto.class), anyString())).thenThrow(new RuntimeException("Error interno"));

        ResponseEntity<GenericResponse> result = tenantPaymentController.intentPayment(pagoDto, status);
        assertEquals(500, result.getStatusCodeValue());
        assertEquals("Error interno", result.getBody().getMessage());
    }
}
