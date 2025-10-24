package com.lealtixservice.repository;

import java.math.BigDecimal;

public interface TenantMenuProductProjection {
    Long getId();
    String getNombre();
    String getDescripcion();
    String getImgUrl();
    BigDecimal getPrecio();
    Boolean getActive();

    Long getCategoryId();
    String getCategoryNombre();
    String getCategoryDescripcion();

    Long getTenantId();
}

