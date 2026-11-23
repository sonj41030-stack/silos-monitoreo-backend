package com.sonj.mssilos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "silos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Silo {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String producto;

    @Column(name = "capacidad_tn", nullable = false)
    private double capacidadTn;

    @Column(name = "toneladas_actuales", nullable = false)
    private double toneladasActuales;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "granja_id", nullable = false)
    private Granja granja;
}