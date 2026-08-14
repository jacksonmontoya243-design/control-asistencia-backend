package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.TipoAsistencia;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la consulta de asistencias.
 * Enriquece la información del registro con los datos del empleado
 * (nombre, documento y cargo), manteniendo intacto el DTO
 * {@link AsistenciaResponse} usado por el dashboard y el escáner.
 */
public record AsistenciaConsultaResponse(
        Long id,
        Long empleadoId,
        String nombreEmpleado,
        String documento,
        String cargo,
        LocalDateTime fechaHora,
        TipoAsistencia tipo,
        boolean demo
) {
}
