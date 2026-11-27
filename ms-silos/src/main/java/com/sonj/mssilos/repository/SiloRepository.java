package com.sonj.mssilos.repository;

import com.sonj.mssilos.entity.Silo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiloRepository extends JpaRepository<Silo, String> {

    @EntityGraph(attributePaths = "granja")
    List<Silo> findByActivoTrueOrderByNombreAsc();

    @EntityGraph(attributePaths = "granja")
    Optional<Silo> findByIdAndActivoTrue(String id);
}