package com.sonj.msauth.dto;

import com.sonj.msauth.entity.RolUsuario;
import jakarta.validation.constraints.NotNull;

public record CambiarRolRequest(
        @NotNull(message = "El rol es obligatorio.") RolUsuario rol
) {
}