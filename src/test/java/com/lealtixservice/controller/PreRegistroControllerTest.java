package com.lealtixservice.controller;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.exception.EmailAlreadyRegisteredException;
import com.lealtixservice.service.PreRegistroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreRegistroControllerTest {
    @Mock
    private PreRegistroService preRegistroService;

    @InjectMocks
    private PreRegistroController preRegistroController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearPreRegistro_retorna201CuandoExito() throws IOException {
        PreRegistroDTO dto = PreRegistroDTO.builder()
                .nombre("Juan")
                .email("juan@mail.com")
                .build();

        PreRegistro preRegistro = PreRegistro.builder()
                .nombre("Juan")
                .email("juan@mail.com")
                .status("PENDING")
                .build();

        when(preRegistroService.register(dto)).thenReturn(preRegistro);

        ResponseEntity<?> response = preRegistroController.crearPreRegistro(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void crearPreRegistro_retorna409CuandoEmailYaRegistrado() throws IOException {
        PreRegistroDTO dto = PreRegistroDTO.builder()
                .nombre("Ana")
                .email("ana@mail.com")
                .build();

        when(preRegistroService.register(dto)).thenThrow(new EmailAlreadyRegisteredException("El email ya está registrado"));

        ResponseEntity<?> response = preRegistroController.crearPreRegistro(dto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}

