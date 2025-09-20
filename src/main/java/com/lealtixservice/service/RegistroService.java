package com.lealtixservice.service;

import com.lealtixservice.dto.PagoDto;
import com.lealtixservice.dto.RegistroDto;
import com.lealtixservice.entity.Tenant;

public interface RegistroService {

    Tenant register(RegistroDto dto);

    void registrarPago(PagoDto dto);
}

