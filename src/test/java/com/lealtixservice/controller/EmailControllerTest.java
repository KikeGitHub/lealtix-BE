package com.lealtixservice.controller;

import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.service.Emailservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerTest {

    @Mock
    private Emailservice emailService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmailWithTemplate_success() throws IOException {
        EmailDTO emailDTO = new EmailDTO();
        doNothing().when(emailService).sendEmailWithTemplate(emailDTO);

        ResponseEntity<String> response = emailController.sendEmailWithTemplate(emailDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Correo enviado exitosamente", response.getBody());
        verify(emailService, times(1)).sendEmailWithTemplate(emailDTO);
    }

    @Test
    void sendEmailWithTemplate_failure() throws IOException {
        EmailDTO emailDTO = new EmailDTO();
        doThrow(new IOException("Fallo de envío")).when(emailService).sendEmailWithTemplate(emailDTO);

        ResponseEntity<String> response = emailController.sendEmailWithTemplate(emailDTO);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Error al enviar el correo"));
        verify(emailService, times(1)).sendEmailWithTemplate(emailDTO);
    }
}

