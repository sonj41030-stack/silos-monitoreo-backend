package com.sonj.mssilos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "granjas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Granja {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 200)
    private String ubicacion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @Version
    private Long version;

    @Builder.Default
    @OneToMany(mappedBy = "granja")
    private List<Silo> silos = new ArrayList<>();
}