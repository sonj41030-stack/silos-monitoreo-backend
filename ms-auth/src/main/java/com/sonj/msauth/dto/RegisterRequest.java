package com.sonj.msauth.dto;

import com.sonj.msauth.entity.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "Por favor ingresa tu nombre completo.") String nombre,
        @NotBlank(message = "Por favor ingresa un correo electrónico válido.") String email,
        @NotBlank(message = "La contraseña debe tener al menos 6 caracteres.") String contrasena,
        @NotNull(message = "La fecha de nacimiento es obligatoria (formato yyyy-MM-dd).") LocalDate fechaNacimiento,
        @NotNull(message = "El rol es obligatorio.") RolUsuario rol
) {
}
