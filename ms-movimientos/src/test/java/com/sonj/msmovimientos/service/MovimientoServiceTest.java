package com.sonj.msmovimientos.service;

import com.sonj.msmovimientos.dto.MovimientoRequest;
import com.sonj.msmovimientos.dto.MovimientoResponse;
import com.sonj.msmovimientos.entity.Movimiento;
import com.sonj.msmovimientos.entity.TipoMovimiento;
import com.sonj.msmovimientos.exception.ApiException;
import com.sonj.msmovimientos.repository.MovimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository repository;

    @InjectMocks
    private MovimientoService service;

    private Movimiento movimiento(String id, String siloId, TipoMovimiento tipo) {
        return Movimiento.builder().id(id).siloId(siloId).tipo(tipo)
                .cantidadKg(new BigDecimal("500.00")).fechaHora(LocalDateTime.now())
                .usuarioEmail("operador@silos.com").build();
    }

    @Test
    void registrar_guardaConUsuarioYFecha() {
        when(repository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoResponse r = service.registrar(
                new MovimientoRequest("silo-1", TipoMovimiento.ENTRADA, new BigDecimal("1200.50"), "Camion 3"),
                "operador@silos.com");

        ArgumentCaptor<Movimiento> cap = ArgumentCaptor.forClass(Movimiento.class);
        verify(repository).save(cap.capture());
        assertEquals("operador@silos.com", cap.getValue().getUsuarioEmail());
        assertNotNull(cap.getValue().getFechaHora());
        assertEquals(TipoMovimiento.ENTRADA, r.tipo());
        assertEquals(new BigDecimal("1200.50"), r.cantidadKg());
    }

    @Test
    void registrar_recortaEspaciosDelSilo() {
        when(repository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoResponse r = service.registrar(
                new MovimientoRequest("  silo-1  ", TipoMovimiento.SALIDA, BigDecimal.TEN, null),
                "operador@silos.com");

        assertEquals("silo-1", r.siloId());
    }

    @Test
    void registrar_observacionEnBlancoQuedaNula() {
        when(repository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoResponse r = service.registrar(
                new MovimientoRequest("silo-1", TipoMovimiento.SALIDA, BigDecimal.TEN, "   "),
                "operador@silos.com");

        assertNull(r.observacion());
    }

    @Test
    void listar_conSilo_filtraPorSilo() {
        when(repository.findBySiloIdOrderByFechaHoraDesc("silo-1"))
                .thenReturn(List.of(movimiento("1", "silo-1", TipoMovimiento.ENTRADA)));

        List<MovimientoResponse> lista = service.listar("silo-1");

        assertEquals(1, lista.size());
        verify(repository, never()).findAllByOrderByFechaHoraDesc();
    }

    @Test
    void listar_sinSilo_devuelveTodos() {
        when(repository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(
                movimiento("1", "silo-1", TipoMovimiento.ENTRADA),
                movimiento("2", "silo-2", TipoMovimiento.SALIDA)));

        assertEquals(2, service.listar(null).size());
    }

    @Test
    void obtener_existente_devuelveMovimiento() {
        when(repository.findById("1")).thenReturn(Optional.of(movimiento("1", "silo-1", TipoMovimiento.ENTRADA)));

        assertEquals("1", service.obtener("1").id());
    }

    @Test
    void obtener_inexistente_lanza404() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.obtener("x"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}