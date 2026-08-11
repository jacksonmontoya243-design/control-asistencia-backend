package com.example.controlasistenciabackend.dto;

import com.example.controlasistenciabackend.entity.Role;

/**
 * DTO de respuesta para autenticación exitosa.
 * Contiene el token JWT y datos básicos del usuario autenticado.
 */
public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        String username,
        Role role
) {
}