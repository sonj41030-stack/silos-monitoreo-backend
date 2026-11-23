package com.sonj.msmovimientos.dto;

import com.sonj.msmovimientos.entity.TipoMovimiento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MovimientoRequest(
        @NotBlank(message = "El silo es obligatorio.")
        String siloId,

        @NotNull(message = "El tipo es obligatorio (ENTRADA o SALIDA).")
        TipoMovimiento tipo,

        @NotNull(message = "La cantidad es obligatoria.")
        @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor a 0.")
        BigDecimal cantidadKg,

        @Size(max = 255, message = "La observacion admite hasta 255 caracteres.")
        String observacion
) {
}