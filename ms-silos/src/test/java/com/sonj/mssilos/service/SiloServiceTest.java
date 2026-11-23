package com.sonj.mssilos.service;

import com.sonj.mssilos.dto.GranjaResponse;
import com.sonj.mssilos.dto.SiloResponse;
import com.sonj.mssilos.entity.EstadoSilo;
import com.sonj.mssilos.entity.Granja;
import com.sonj.mssilos.entity.Silo;
import com.sonj.mssilos.exception.ApiException;
import com.sonj.mssilos.repository.GranjaRepository;
import com.sonj.mssilos.repository.SiloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SiloServiceTest {

    @Mock
    private SiloRepository siloRepo;

    @Mock
    private GranjaRepository granjaRepo;

    @InjectMocks
    private SiloService service;

    private Granja granja;

    @BeforeEach
    void setUp() {
        granja = Granja.builder().id("g1").nombre("Granja Uno").ubicacion("Melipilla").build();
    }

    private Silo silo(String id, String nombre, double capacidad, double actual) {
        return Silo.builder().id(id).nombre(nombre).producto("Maiz")
                .capacidadTn(capacidad).toneladasActuales(actual).granja(granja).build();
    }

    private void tresSilos() {
        when(siloRepo.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of(
                silo("s1", "Silo 1", 500, 420),
                silo("s2", "Silo 2", 400, 100),
                silo("s3", "Silo 3", 300, 30)));
    }

    @Test
    void listarSilos_sinFiltro_devuelveTodosConSuEstado() {
        tresSilos();

        List<SiloResponse> r = service.listarSilos(null);

        assertEquals(3, r.size());
        assertEquals(EstadoSilo.NORMAL, r.get(0).estado());
        assertEquals(EstadoSilo.ADVERTENCIA, r.get(1).estado());
        assertEquals(EstadoSilo.CRITICO, r.get(2).estado());
    }

    @Test
    void listarSilos_filtroCritico_soloDevuelveCriticos() {
        tresSilos();

        List<SiloResponse> r = service.listarSilos("CRITICO");

        assertEquals(1, r.size());
        assertEquals("s3", r.get(0).id());
    }

    @Test
    void listarSilos_filtroEnMinusculas_tambienFunciona() {
        tresSilos();

        List<SiloResponse> r = service.listarSilos("advertencia");

        assertEquals(1, r.size());
        assertEquals("s2", r.get(0).id());
    }

    @Test
    void listarSilos_estadoInvalido_da400() {
        ApiException ex = assertThrows(ApiException.class, () -> service.listarSilos("MUY_LLENO"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void obtenerSilo_existente_calculaPorcentajeYGranja() {
        when(siloRepo.findByIdAndActivoTrue("s1")).thenReturn(Optional.of(silo("s1", "Silo 1", 500, 420)));

        SiloResponse r = service.obtenerSilo("s1");

        assertEquals(84.0, r.porcentaje(), 0.01);
        assertEquals("Granja Uno", r.granjaNombre());
    }

    @Test
    void obtenerSilo_inexistente_da404() {
        when(siloRepo.findByIdAndActivoTrue("zzz")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.obtenerSilo("zzz"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void listarGranjas_incluyeSusSilosOrdenadosPorNombre() {
        granja.getSilos().add(silo("s2", "Silo B", 400, 100));
        granja.getSilos().add(silo("s1", "Silo A", 500, 420));
        when(granjaRepo.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of(granja));

        List<GranjaResponse> r = service.listarGranjas();

        assertEquals(1, r.size());
        assertEquals("Silo A", r.get(0).silos().get(0).nombre());
        assertEquals("Silo B", r.get(0).silos().get(1).nombre());
    }

    @Test
    void obtenerGranja_inexistente_da404() {
        when(granjaRepo.findByIdAndActivoTrue("zzz")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.obtenerGranja("zzz"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}