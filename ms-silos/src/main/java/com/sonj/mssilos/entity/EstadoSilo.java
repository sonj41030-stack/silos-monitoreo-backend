package com.sonj.mssilos.entity;

public enum EstadoSilo {
    CRITICO,
    ADVERTENCIA,
    NORMAL;

    public static EstadoSilo desdePorcentaje(double porcentaje) {
        if (porcentaje < 15) {
            return CRITICO;
        }
        if (porcentaje < 30) {
            return ADVERTENCIA;
        }
        return NORMAL;
    }
}