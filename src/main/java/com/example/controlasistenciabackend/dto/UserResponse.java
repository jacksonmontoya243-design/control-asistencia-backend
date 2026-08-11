package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.Role;

/**
 * DTO de respuesta para la gestión de usuarios.
 * Nunca expone la contraseña.
 */
public record UserResponse(
        Long id,
        String username,
        Role role
) {
}