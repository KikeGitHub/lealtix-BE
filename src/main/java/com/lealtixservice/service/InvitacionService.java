package com.lealtixservice.service;

import com.lealtixservice.dto.InvitacionDTO;
import com.lealtixservice.entity.Invitacion;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.exception.InvalidOrExpiredTokenException;
import com.lealtixservice.repository.InvitacionRepository;
import com.lealtixservice.repository.PreRegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitacionService {
    private final InvitacionRepository invitacionRepository;
    private final PreRegistroRepository preRegistroRepository;

    @Transactional
    public Invitacion generarInvitacion(InvitacionDTO dto) {
        PreRegistro preRegistro = preRegistroRepository.findById(dto.getPreRegistroId())
                .orElseThrow(() -> new IllegalArgumentException("PreRegistro no encontrado"));
        String token = UUID.randomUUID().toString();
        Invitacion invitacion = Invitacion.builder()
                .preRegistro(preRegistro)
                .token(token)
                .fechaEnvio(LocalDateTime.now())
                .estado("ENVIADO")
                .build();
        return invitacionRepository.save(invitacion);
    }

    public Invitacion verificarToken(String token) {
        Optional<Invitacion> invitacionOpt = invitacionRepository.findByToken(token);
        Invitacion invitacion = invitacionOpt.orElseThrow(() -> new InvalidOrExpiredTokenException("Token inválido o expirado"));
        if (!"ENVIADO".equals(invitacion.getEstado())) {
            throw new InvalidOrExpiredTokenException("Token ya fue aceptado o expiró");
        }
        // Aquí podrías agregar lógica de expiración por tiempo si se requiere
        return invitacion;
    }
}

