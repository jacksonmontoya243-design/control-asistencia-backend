package com.example.controlasistenciabackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Usuario: Representa la tabla de usuarios en la base de datos.
 * Almacena las credenciales y el rol para el proceso de autenticación y autorización.
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El nombre de usuario debe ser único para evitar duplicados en el registro
    @Column(unique = true, nullable = false)
    private String username;

    // La contraseña debe ser obligatoria
    @Column(nullable = false)
    private String password;

    // Rol del usuario para control de acceso (ADMIN, SUPERVISOR, EMPLEADO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}