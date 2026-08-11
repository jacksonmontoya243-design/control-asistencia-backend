package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.TipoAsistencia;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para una asistencia registrada.
 */
public record AsistenciaResponse(
        Long id,
        Long empleadoId,
        LocalDateTime fechaHora,
        TipoAsistencia tipo
) {
}