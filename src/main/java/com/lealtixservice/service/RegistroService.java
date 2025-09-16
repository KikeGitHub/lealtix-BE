package com.lealtixservice.service;

import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.dto.RegistroDto;

public interface RegistroService {

    void register(RegistroDto dto);

    void registrarPago(PagoDto dto);
}

