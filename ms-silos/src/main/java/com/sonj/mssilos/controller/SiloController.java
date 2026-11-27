package com.sonj.mssilos.controller;

import com.sonj.mssilos.dto.AjusteSiloRequest;
import com.sonj.mssilos.dto.SiloRequest;
import com.sonj.mssilos.dto.SiloResponse;
import com.sonj.mssilos.service.SiloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/silos")
@RequiredArgsConstructor
@Tag(name = "Silos")
@SecurityRequirement(name = "bearerAuth")
public class SiloController {

    private final SiloService service;

    @GetMapping
    @Operation(summary = "Lista los silos, con filtro opcional por estado (CRITICO, ADVERTENCIA, NORMAL)")
    public List<SiloResponse> listar(@RequestParam(required = false) String estado) {
        return service.listarSilos(estado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de un silo")
    public SiloResponse obtener(@PathVariable String id) {
        return service.obtenerSilo(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crea un silo (solo administrador)")
    public SiloResponse crear(@RequestBody SiloRequest req) {
        return service.crearSilo(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Edita un silo (solo administrador)")
    public SiloResponse actualizar(@PathVariable String id, @RequestBody SiloRequest req) {
        return service.actualizarSilo(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Elimina un silo, borrado logico (solo administrador)")
    public void eliminar(@PathVariable String id) {
        service.eliminarSilo(id);
    }

    @PostMapping("/{id}/movimientos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADOR')")
    @Operation(summary = "Ajusta el nivel del silo con una ENTRADA o SALIDA en kilos")
    public SiloResponse ajustar(@PathVariable String id, @RequestBody AjusteSiloRequest req) {
        return service.ajustarSilo(id, req);
    }
}