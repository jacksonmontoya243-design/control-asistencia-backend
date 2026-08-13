package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de usuarios por parte del administrador.
 * Permite asignar rol y asociar un empleado.
 */
public record CreateUserRequest(
        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Role role,

        Long empleadoId
) {
}