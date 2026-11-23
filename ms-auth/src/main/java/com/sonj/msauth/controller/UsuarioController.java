package com.sonj.msauth.controller;

import com.sonj.msauth.dto.CambiarRolRequest;
import com.sonj.msauth.dto.UsuarioAdminResponse;
import com.sonj.msauth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios (solo administrador)")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @Operation(summary = "Lista todos los usuarios")
    public List<UsuarioAdminResponse> listar(Authentication auth) {
        return service.listar(auth.getName());
    }

    @PutMapping("/{id}/rol")
    @Operation(summary = "Cambia el rol de un usuario")
    public UsuarioAdminResponse cambiarRol(@PathVariable String id,
                                           @Valid @RequestBody CambiarRolRequest request,
                                           Authentication auth) {
        return service.cambiarRol(auth.getName(), id, request.rol());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactiva un usuario (baja lógica)")
    public UsuarioAdminResponse desactivar(@PathVariable String id, Authentication auth) {
        return service.cambiarEstado(auth.getName(), id, false);
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Reactiva un usuario")
    public UsuarioAdminResponse activar(@PathVariable String id, Authentication auth) {
        return service.cambiarEstado(auth.getName(), id, true);
    }
}