package com.lealtixservice.service;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.exception.EmailAlreadyRegisteredException;
import com.lealtixservice.repository.PreRegistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PreRegistroServiceTest {
    @Mock
    private PreRegistroRepository preRegistroRepository;

    @InjectMocks
    private PreRegistroService preRegistroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void emailExists_returnsTrueIfEmailFound() {
        when(preRegistroRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(new PreRegistro()));
        assertTrue(preRegistroService.emailExists("test@mail.com"));
    }

    @Test
    void emailExists_returnsFalseIfEmailNotFound() {
        when(preRegistroRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        assertFalse(preRegistroService.emailExists("test@mail.com"));
    }

    @Test
    void register_throwsExceptionIfEmailExists() {
        PreRegistroDTO dto = PreRegistroDTO.builder().email("test@mail.com").nombre("Test").build();
        when(preRegistroRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(new PreRegistro()));
        assertThrows(EmailAlreadyRegisteredException.class, () -> preRegistroService.register(dto));
    }

    @Test
    void register_savesPreRegistroIfEmailNotExists() {
        PreRegistroDTO dto = PreRegistroDTO.builder().email("nuevo@mail.com").nombre("Nuevo").build();
        when(preRegistroRepository.findByEmail("nuevo@mail.com")).thenReturn(Optional.empty());
        PreRegistro preRegistroMock = PreRegistro.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .status("PENDING")
                .fechaRegistro(LocalDateTime.now())
                .build();
        when(preRegistroRepository.save(any(PreRegistro.class))).thenReturn(preRegistroMock);
        PreRegistro result = preRegistroService.register(dto);
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals("PENDING", result.getStatus());
    }
}

