package com.example.controlasistenciabackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Asistencia: Representa la tabla de registros de entrada/salida.
 */
@Entity
@Table(name = "asistencias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del empleado que registra la asistencia
    @Column(nullable = false)
    private Long empleadoId;

    // Fecha y hora del registro
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    // Tipo de registro: ENTRADA o SALIDA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAsistencia tipo;
}