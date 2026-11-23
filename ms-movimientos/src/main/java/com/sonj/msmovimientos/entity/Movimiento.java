package com.sonj.msmovimientos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos", indexes = @Index(name = "idx_mov_silo", columnList = "silo_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "silo_id", nullable = false, length = 36)
    private String siloId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimiento tipo;

    @Column(name = "cantidad_kg", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadKg;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "usuario_email", nullable = false)
    private String usuarioEmail;

    @Column(length = 255)
    private String observacion;
}