package com.lealtixservice.service.impl;

import com.lealtixservice.config.SendGridTemplates;
import com.lealtixservice.dto.EmailDTO;
import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.entity.PreRegistro;
import com.lealtixservice.exception.EmailAlreadyRegisteredException;
import com.lealtixservice.repository.PreRegistroRepository;
import com.lealtixservice.service.Emailservice;
import com.lealtixservice.service.InvitationService;
import com.lealtixservice.service.PreRegistroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PreRegistroServiceImpl implements PreRegistroService {

    public static final String INVITED = "INVITED";
    private final Emailservice emailservice;
    private final InvitationService invitationService;
    private final SendGridTemplates sendGridTemplates;
    private final PreRegistroRepository preRegistroRepository;

    public boolean emailExists(String email) {
        return preRegistroRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public PreRegistro register(PreRegistroDTO dto) throws IOException {
        if (emailExists(dto.getEmail())) {
            throw new EmailAlreadyRegisteredException("El email ya está registrado");
        }
        PreRegistro preRegistro = PreRegistro.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .status(INVITED)
                .description("Send invitation email")
                .fechaRegistro(LocalDateTime.now())
                .build();
        var response = preRegistroRepository.save(preRegistro);
        // Generate Invitation
        String link = invitationService.generateInvitation(dto,"lealtix.com");
        // Send PreRegister Email
        EmailDTO emailDTO = EmailDTO.builder()
                .to(dto.getEmail())
                .subject("Gracias por registrarte en Lealtix")
                .templateId(sendGridTemplates.getPreRegistroTemplate())
                .dynamicData(Map.of(
                        "name", dto.getNombre(),
                        "link", link,
                        "logoUrl", "https://res.cloudinary.com/lealtix-media/image/upload/v1759897289/lealtix_logo_transp_qcp5h9.png"
                ))
                .build();
        emailservice.sendEmailWithTemplate(emailDTO);
        return response;
    }

    @Transactional
    public void deletePreRegistro(Long id) {
        if (!preRegistroRepository.existsById(id)) {
            throw new IllegalArgumentException("Pre-registro no encontrado");
        }
        preRegistroRepository.deleteById(id);
    }

    public PreRegistro getPreRegistroByEmail(String email) {
        return preRegistroRepository.findByEmail(email).orElse(null);
    }

    public void sendPreRegistroEmail(String email, String nombre, String url) throws IOException {
        Map<String, Object> data = Map.of(
                "name", nombre,
                "logoUrl", url
        );


    }

    @Override
    public void save(PreRegistro preRegistro) {
        preRegistroRepository.save(preRegistro);
    }
}
