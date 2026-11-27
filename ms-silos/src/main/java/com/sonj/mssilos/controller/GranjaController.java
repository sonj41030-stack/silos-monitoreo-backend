package com.sonj.mssilos.controller;

import com.sonj.mssilos.dto.GranjaResponse;
import com.sonj.mssilos.service.SiloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/granjas")
@RequiredArgsConstructor
@Tag(name = "Granjas")
@SecurityRequirement(name = "bearerAuth")
public class GranjaController {

    private final SiloService service;

    @GetMapping
    @Operation(summary = "Lista las granjas con sus silos")
    public List<GranjaResponse> listar() {
        return service.listarGranjas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una granja")
    public GranjaResponse obtener(@PathVariable String id) {
        return service.obtenerGranja(id);
    }
}