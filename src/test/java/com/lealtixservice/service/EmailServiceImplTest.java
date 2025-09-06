package com.lealtixservice.service;

import com.lealtixservice.service.impl.EmailServiceImpl;
import com.sendgrid.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private SendGrid sendGrid;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmailSuccess() throws IOException {
        Response mockResponse = new Response();
        mockResponse.setStatusCode(202);
        mockResponse.setBody("Accepted");
        mockResponse.setHeaders(null);
        when(sendGrid.api(any(Request.class))).thenReturn(mockResponse);

        assertDoesNotThrow(() -> {
            emailService.sendEmail("fnsl_kike@hotmail.com", "Asunto", "Cuerpo del mensaje");
        });

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(sendGrid, times(1)).api(requestCaptor.capture());
        Request sentRequest = requestCaptor.getValue();
        assertEquals(Method.POST, sentRequest.getMethod());
        assertEquals("mail/send", sentRequest.getEndpoint());
        assertNotNull(sentRequest.getBody());
    }

    @Test
    void testSendEmailThrowsIOException() throws IOException {
        when(sendGrid.api(any(Request.class))).thenThrow(new IOException("Error de red"));
        assertThrows(IOException.class, () -> {
            emailService.sendEmail("test@correo.com", "Asunto", "Cuerpo del mensaje");
        });
    }

}
