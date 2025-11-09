package com.lealtixservice.controller;

import com.lealtixservice.dto.GenericResponse;
import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.dto.ValidateTokenResponse;
import com.lealtixservice.service.InvitationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvitationControllerTest {

    @Mock
    private InvitationService invitationService;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private InvitationController invitationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void invite_shouldReturnSuccessMessage() {
        PreRegistroDTO dto = PreRegistroDTO.builder()
                .nombre("Test")
                .email("test@email.com")
                .build();
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(invitationService.generateInvitation(dto, "127.0.0.1")).thenReturn("token123");

        ResponseEntity<?> response = invitationController.invite(dto, request);
        assertEquals(200, response.getStatusCodeValue());
        verify(invitationService, times(1)).generateInvitation(dto, "127.0.0.1");
    }

    @Test
    void validateToken_shouldReturnResponse() {
        String token = "token123";
        ValidateTokenResponse expectedResponse = ValidateTokenResponse.builder().build();
        when(invitationService.validateToken(token)).thenReturn(expectedResponse);

        ResponseEntity<GenericResponse> response = invitationController.validateToken(token);
        assertEquals(expectedResponse, response.getBody().getObject());
    }
}
