package com.example.controlasistenciabackend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para activar o desactivar un usuario.
 */
public record UpdateActivoRequest(
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo
) {
}