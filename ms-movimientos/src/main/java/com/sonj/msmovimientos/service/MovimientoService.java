package com.sonj.msmovimientos.service;

import com.sonj.msmovimientos.dto.MovimientoRequest;
import com.sonj.msmovimientos.dto.MovimientoResponse;
import com.sonj.msmovimientos.entity.Movimiento;
import com.sonj.msmovimientos.exception.ApiException;
import com.sonj.msmovimientos.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository repository;

    @Transactional
    public MovimientoResponse registrar(MovimientoRequest req, String usuarioEmail) {
        String obs = req.observacion() == null || req.observacion().isBlank()
                ? null : req.observacion().trim();

        Movimiento m = Movimiento.builder()
                .siloId(req.siloId().trim())
                .tipo(req.tipo())
                .cantidadKg(req.cantidadKg())
                .fechaHora(LocalDateTime.now())
                .usuarioEmail(usuarioEmail)
                .observacion(obs)
                .build();

        return MovimientoResponse.from(repository.save(m));
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar(String siloId) {
        List<Movimiento> lista = (siloId == null || siloId.isBlank())
                ? repository.findAllByOrderByFechaHoraDesc()
                : repository.findBySiloIdOrderByFechaHoraDesc(siloId.trim());
        return lista.stream().map(MovimientoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse obtener(String id) {
        return repository.findById(id)
                .map(MovimientoResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Movimiento no encontrado."));
    }
}