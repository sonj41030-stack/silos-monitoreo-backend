package com.sonj.authservice.dto;

public record LoginResponse(
        boolean exito,
        String mensaje,
        String token,
        String usuarioNombre,
        String rol
) {}