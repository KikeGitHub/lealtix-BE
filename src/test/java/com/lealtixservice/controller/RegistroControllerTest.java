package com.lealtixservice.controller;

import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.service.RegistroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroControllerTest {

    @Mock
    private RegistroService registroService;

    @InjectMocks
    private RegistroController registroController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() {
        RegistroDto dto = new RegistroDto();
        doNothing().when(registroService).register(dto);

        ResponseEntity<String> response = registroController.register(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registro exitoso", response.getBody());
        verify(registroService, times(1)).register(dto);
    }

    @Test
    void register_illegalArgumentException() {
        RegistroDto dto = new RegistroDto();
        doThrow(new IllegalArgumentException("Error de registro")).when(registroService).register(dto);

        ResponseEntity<String> response = registroController.register(dto);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error de registro", response.getBody());
        verify(registroService, times(1)).register(dto);
    }

    @Test
    void register_otherException() {
        RegistroDto dto = new RegistroDto();
        doThrow(new RuntimeException()).when(registroService).register(dto);

        ResponseEntity<String> response = registroController.register(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request body", response.getBody());
        verify(registroService, times(1)).register(dto);
    }
}

