package com.sonj.msauth.dto;

public record LoginResponse(
        boolean exito,
        String mensaje,
        String token,
        String usuarioNombre,
        String rol
) {
}
