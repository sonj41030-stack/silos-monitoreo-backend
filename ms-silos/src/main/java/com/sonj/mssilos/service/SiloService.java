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
import com.sonj.mssilos.dto.SiloRequest;
import com.sonj.mssilos.entity.Granja;
import com.sonj.mssilos.entity.Silo;
import java.util.UUID;

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

    @Transactional
    public SiloResponse crearSilo(SiloRequest req) {
        validar(req);
        Granja granja = granjaRepo.findByIdAndActivoTrue(req.granjaId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "La granja indicada no existe."));
        Silo silo = Silo.builder()
                .id(UUID.randomUUID().toString())
                .nombre(req.nombre().trim())
                .producto(req.producto().trim())
                .capacidadTn(req.capacidadTn())
                .toneladasActuales(req.toneladasActuales())
                .granja(granja)
                .build();
        return SiloResponse.from(siloRepo.save(silo));
    }

    @Transactional
    public SiloResponse actualizarSilo(String id, SiloRequest req) {
        validar(req);
        Silo silo = siloRepo.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Silo no encontrado."));
        Granja granja = granjaRepo.findByIdAndActivoTrue(req.granjaId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "La granja indicada no existe."));
        silo.setNombre(req.nombre().trim());
        silo.setProducto(req.producto().trim());
        silo.setCapacidadTn(req.capacidadTn());
        silo.setToneladasActuales(req.toneladasActuales());
        silo.setGranja(granja);
        return SiloResponse.from(siloRepo.save(silo));
    }

    @Transactional
    public void eliminarSilo(String id) {
        Silo silo = siloRepo.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Silo no encontrado."));
        silo.setActivo(false);
        siloRepo.save(silo);
    }

    private void validar(SiloRequest req) {
        if (req == null || req.nombre() == null || req.nombre().isBlank()
                || req.producto() == null || req.producto().isBlank()
                || req.granjaId() == null || req.granjaId().isBlank()
                || req.capacidadTn() == null || req.toneladasActuales() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Faltan datos: nombre, producto, capacidadTn, toneladasActuales y granjaId son obligatorios.");
        }
        if (req.capacidadTn() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La capacidad debe ser mayor a 0.");
        }
        if (req.toneladasActuales() < 0 || req.toneladasActuales() > req.capacidadTn()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Las toneladas actuales deben estar entre 0 y la capacidad.");
        }
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