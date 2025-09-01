package com.lealtixservice.service;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.exception.EmailAlreadyRegisteredException;
import com.lealtixservice.repository.PreRegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PreRegistroService {
    private final PreRegistroRepository preRegistroRepository;

    public boolean emailExists(String email) {
        return preRegistroRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public PreRegistro register(PreRegistroDTO dto) {
        if (emailExists(dto.getEmail())) {
            throw new EmailAlreadyRegisteredException("El email ya está registrado");
        }
        PreRegistro preRegistro = PreRegistro.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .status("PENDING")
                .fechaRegistro(LocalDateTime.now())
                .build();
        return preRegistroRepository.save(preRegistro);
    }
}

