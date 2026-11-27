package com.sonj.msauth.dto;

import com.sonj.msauth.entity.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank(message = "Por favor ingresa tu correo electrónico.") String email,
        @NotBlank(message = "Por favor ingresa tu contraseña.") String contrasena,
        @NotNull(message = "El rol es obligatorio.") RolUsuario rol
) {
}
