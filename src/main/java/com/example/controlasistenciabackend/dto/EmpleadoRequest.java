package com.example.controlasistenciabackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear o actualizar un empleado.
 * Reemplaza el uso directo de la entidad Empleado como cuerpo de la petición.
 */
public record EmpleadoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El documento es obligatorio")
        String documento,

        @NotBlank(message = "El cargo es obligatorio")
        String cargo
) {
}