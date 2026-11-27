package com.sonj.msauth.dto;

import java.time.LocalDate;

public record UsuarioAdminResponse(
        String id,
        String nombre,
        String email,
        LocalDate fechaNacimiento,
        String rol,
        boolean activo
) {
}