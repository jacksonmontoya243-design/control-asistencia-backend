package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la petición de registro de usuarios.
 */
public record RegisterRequest(
        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        Role role
) {
}