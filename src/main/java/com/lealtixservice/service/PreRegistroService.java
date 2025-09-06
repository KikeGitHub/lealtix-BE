package com.lealtixservice.service;

import com.lealtixservice.dto.PreRegistroDTO;
import com.lealtixservice.entity.PreRegistro;

import java.io.IOException;

public interface PreRegistroService {

    public PreRegistro register(PreRegistroDTO dto) throws IOException;

    void deletePreRegistro(Long id);

    public PreRegistro getPreRegistroByEmail(String email);

    public boolean emailExists(String email);

    public void sendPreRegistroEmail(String email, String nombre, String url) throws IOException;
}
