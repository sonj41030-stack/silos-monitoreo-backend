package com.sonj.mssilos.dto;

import com.sonj.mssilos.entity.EstadoSilo;
import com.sonj.mssilos.entity.Silo;

public record SiloResponse(
        String id,
        String nombre,
        String producto,
        double capacidadTn,
        double toneladasActuales,
        double porcentaje,
        EstadoSilo estado,
        String granjaId,
        String granjaNombre
) {

    public static SiloResponse from(Silo s) {
        double pct = s.getCapacidadTn() > 0 ? s.getToneladasActuales() / s.getCapacidadTn() * 100 : 0;
        return new SiloResponse(
                s.getId(), s.getNombre(), s.getProducto(),
                s.getCapacidadTn(), s.getToneladasActuales(),
                Math.round(pct * 10.0) / 10.0,
                EstadoSilo.desdePorcentaje(pct),
                s.getGranja().getId(), s.getGranja().getNombre());
    }
}