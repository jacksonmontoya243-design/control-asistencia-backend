package com.example.controlasistenciabackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la petición de inicio de sesión.
 */
public record LoginRequest(
        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}