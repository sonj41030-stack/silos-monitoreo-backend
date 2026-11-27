package com.sonj.mssilos.dto;

import com.sonj.mssilos.entity.Granja;
import com.sonj.mssilos.entity.Silo;

import java.util.Comparator;
import java.util.List;

public record GranjaResponse(
        String id,
        String nombre,
        String ubicacion,
        List<SiloResponse> silos
) {

    public static GranjaResponse from(Granja g) {
        List<SiloResponse> silos = g.getSilos().stream()
                .filter(Silo::isActivo)
                .sorted(Comparator.comparing(Silo::getNombre))
                .map(SiloResponse::from)
                .toList();
        return new GranjaResponse(g.getId(), g.getNombre(), g.getUbicacion(), silos);
    }
}