package com.lealtixservice.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Solicitud de registro de merma(s) a partir de una comanda. */
@Data
public class MermaRequest {

    private Long tenantId;

    /** Id de la comanda origen */
    private String orderId;

    /** Insumos/productos marcados como merma en el modal */
    private List<MermaItemRequest> items = new ArrayList<>();

    /** Tipificación de la salida No-Venta (default OPERATIVA) */
    private String tipoMerma;

    @Data
    public static class MermaItemRequest {
        private Long insumoId;
        private String insumoNombre;
        private Long productoId;
        private String productoNombre;
        private Double cantidad;
        private String unidad;
    }
}