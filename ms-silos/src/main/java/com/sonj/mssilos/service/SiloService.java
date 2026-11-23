package com.sonj.mssilos.service;

import com.sonj.mssilos.dto.GranjaResponse;
import com.sonj.mssilos.dto.SiloResponse;
import com.sonj.mssilos.entity.EstadoSilo;
import com.sonj.mssilos.exception.ApiException;
import com.sonj.mssilos.repository.GranjaRepository;
import com.sonj.mssilos.repository.SiloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiloService {

    private final SiloRepository siloRepo;
    private final GranjaRepository granjaRepo;

    public List<SiloResponse> listarSilos(String estado) {
        EstadoSilo filtro = parsearEstado(estado);
        return siloRepo.findByActivoTrueOrderByNombreAsc().stream()
                .map(SiloResponse::from)
                .filter(s -> filtro == null || s.estado() == filtro)
                .toList();
    }

    public SiloResponse obtenerSilo(String id) {
        return siloRepo.findByIdAndActivoTrue(id)
                .map(SiloResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Silo no encontrado."));
    }

    public List<GranjaResponse> listarGranjas() {
        return granjaRepo.findByActivoTrueOrderByNombreAsc().stream()
                .map(GranjaResponse::from)
                .toList();
    }

    public GranjaResponse obtenerGranja(String id) {
        return granjaRepo.findByIdAndActivoTrue(id)
                .map(GranjaResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Granja no encontrada."));
    }

    private EstadoSilo parsearEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        try {
            return EstadoSilo.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Estado inválido. Usa CRITICO, ADVERTENCIA o NORMAL.");
        }
    }
}