package com.sonj.mssilos.repository;

import com.sonj.mssilos.entity.Granja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GranjaRepository extends JpaRepository<Granja, String> {

    List<Granja> findByActivoTrueOrderByNombreAsc();

    Optional<Granja> findByIdAndActivoTrue(String id);
}