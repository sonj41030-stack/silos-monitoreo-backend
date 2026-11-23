package com.sonj.msmovimientos.repository;

import com.sonj.msmovimientos.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, String> {

    List<Movimiento> findBySiloIdOrderByFechaHoraDesc(String siloId);

    List<Movimiento> findAllByOrderByFechaHoraDesc();
}