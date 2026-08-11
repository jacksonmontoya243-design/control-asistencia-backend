package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.Role;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el rol de un usuario.
 */
public record UpdateRoleRequest(
        @NotNull(message = "El rol es obligatorio")
        Role role
) {
}