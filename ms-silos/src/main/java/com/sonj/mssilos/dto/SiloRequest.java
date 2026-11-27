package com.sonj.mssilos.dto;

public record SiloRequest(
        String nombre,
        String producto,
        Double capacidadTn,
        Double toneladasActuales,
        String granjaId
) {
}