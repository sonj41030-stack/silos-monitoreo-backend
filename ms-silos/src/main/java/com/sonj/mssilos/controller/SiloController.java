package com.sonj.mssilos.controller;

import com.sonj.mssilos.dto.SiloResponse;
import com.sonj.mssilos.service.SiloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}