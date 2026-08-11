package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.TipoAsistencia;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para registrar una asistencia (entrada o salida).
 */
public record AsistenciaRequest(
        @NotNull(message = "El empleado es obligatorio")
        Long empleadoId,

        @NotNull(message = "El tipo de asistencia es obligatorio")
        TipoAsistencia tipo
) {
}