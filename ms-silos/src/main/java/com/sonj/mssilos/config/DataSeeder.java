package com.sonj.mssilos.config;

import com.sonj.mssilos.entity.Granja;
import com.sonj.mssilos.entity.Silo;
import com.sonj.mssilos.repository.GranjaRepository;
import com.sonj.mssilos.repository.SiloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final GranjaRepository granjaRepo;
    private final SiloRepository siloRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (granjaRepo.count() > 0) {
            return;
        }
        Granja aromos = granja("Granja Los Aromos", "Melipilla, Región Metropolitana");
        Granja sauce = granja("Granja El Sauce", "Curacaví, Región Metropolitana");
        Granja sanPedro = granja("Granja San Pedro", "San Pedro, Región Metropolitana");
        granjaRepo.saveAll(List.of(aromos, sauce, sanPedro));

        siloRepo.saveAll(List.of(
                silo(aromos, "Silo A1", "Maíz", 500, 420),
                silo(aromos, "Silo A2", "Trigo", 400, 100),
                silo(aromos, "Silo A3", "Maíz", 300, 30),
                silo(sauce, "Silo B1", "Avena", 250, 200),
                silo(sauce, "Silo B2", "Cebada", 350, 60),
                silo(sanPedro, "Silo C1", "Trigo", 600, 72),
                silo(sanPedro, "Silo C2", "Maíz", 450, 300)));
        log.info("Datos de prueba creados: 3 granjas y 7 silos");
    }

    private Granja granja(String nombre, String ubicacion) {
        return Granja.builder().id(UUID.randomUUID().toString()).nombre(nombre).ubicacion(ubicacion).build();
    }

    private Silo silo(Granja g, String nombre, String producto, double capacidad, double actual) {
        return Silo.builder().id(UUID.randomUUID().toString()).nombre(nombre).producto(producto)
                .capacidadTn(capacidad).toneladasActuales(actual).granja(g).build();
    }
}