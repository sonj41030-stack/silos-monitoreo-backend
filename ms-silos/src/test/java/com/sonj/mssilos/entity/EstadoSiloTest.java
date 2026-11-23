package com.sonj.mssilos.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadoSiloTest {

    @Test
    void menorA15EsCritico() {
        assertEquals(EstadoSilo.CRITICO, EstadoSilo.desdePorcentaje(14.9));
    }

    @Test
    void exactamente15EsAdvertencia() {
        assertEquals(EstadoSilo.ADVERTENCIA, EstadoSilo.desdePorcentaje(15));
    }

    @Test
    void justoBajo30EsAdvertencia() {
        assertEquals(EstadoSilo.ADVERTENCIA, EstadoSilo.desdePorcentaje(29.9));
    }

    @Test
    void exactamente30EsNormal() {
        assertEquals(EstadoSilo.NORMAL, EstadoSilo.desdePorcentaje(30));
    }

    @Test
    void ceroEsCritico() {
        assertEquals(EstadoSilo.CRITICO, EstadoSilo.desdePorcentaje(0));
    }

    @Test
    void cienEsNormal() {
        assertEquals(EstadoSilo.NORMAL, EstadoSilo.desdePorcentaje(100));
    }
}