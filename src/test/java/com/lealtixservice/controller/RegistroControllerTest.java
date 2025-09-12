package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
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

        ResponseEntity<GenericResponse> response = registroController.register(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(new GenericResponse("201", "SUCCESS", null), response.getBody());
        verify(registroService, times(1)).register(dto);
    }

    @Test
    void register_illegalArgumentException() {
        RegistroDto dto = new RegistroDto();
        doThrow(new IllegalArgumentException("Error de registro")).when(registroService).register(dto);

        ResponseEntity<GenericResponse> response = registroController.register(dto);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(new GenericResponse("500", "Error de registro", null), response.getBody());
        verify(registroService, times(1)).register(dto);
    }

}
