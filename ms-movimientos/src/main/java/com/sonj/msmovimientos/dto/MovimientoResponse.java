package com.sonj.msmovimientos.dto;

import com.sonj.msmovimientos.entity.Movimiento;
import com.sonj.msmovimientos.entity.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoResponse(
        String id,
        String siloId,
        TipoMovimiento tipo,
        BigDecimal cantidadKg,
        LocalDateTime fechaHora,
        String usuarioEmail,
        String observacion
) {

    public static MovimientoResponse from(Movimiento m) {
        return new MovimientoResponse(m.getId(), m.getSiloId(), m.getTipo(), m.getCantidadKg(),
                m.getFechaHora(), m.getUsuarioEmail(), m.getObservacion());
    }
}