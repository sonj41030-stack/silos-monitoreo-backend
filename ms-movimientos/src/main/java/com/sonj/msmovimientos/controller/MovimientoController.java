package com.sonj.msmovimientos.controller;

import com.sonj.msmovimientos.dto.MovimientoRequest;
import com.sonj.msmovimientos.dto.MovimientoResponse;
import com.sonj.msmovimientos.service.MovimientoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MovimientoController {

    private final MovimientoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponse registrar(@Valid @RequestBody MovimientoRequest req, Authentication auth) {
        return service.registrar(req, auth.getName());
    }

    @GetMapping
    public List<MovimientoResponse> listar(@RequestParam(required = false) String siloId) {
        return service.listar(siloId);
    }

    @GetMapping("/{id}")
    public MovimientoResponse obtener(@PathVariable String id) {
        return service.obtener(id);
    }
}