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

    /**
     * Registra un nuevo PreRegistro si el email no está ya registrado.
     * @param dto El DTO con los datos del pre-registro.
     * @return El PreRegistro creado.
     * @throws EmailAlreadyRegisteredException si el email ya está registrado.
     */
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

    /**
     * Elimina un PreRegistro por su id.
     * @param id El id del PreRegistro a eliminar.
     * @throws IllegalArgumentException si el PreRegistro no existe.
     */
    @Transactional
    public void deletePreRegistro(Long id) {
        if (!preRegistroRepository.existsById(id)) {
            throw new IllegalArgumentException("Pre-registro no encontrado");
        }
        preRegistroRepository.deleteById(id);
    }

    /**
     * Obtiene un PreRegistro por su email.
     * @param email El email del PreRegistro a buscar.
     * @return El PreRegistro encontrado o null si no existe.
     */
    public PreRegistro getPreRegistroByEmail(String email) {
        return preRegistroRepository.findByEmail(email).orElse(null);
    }
}
