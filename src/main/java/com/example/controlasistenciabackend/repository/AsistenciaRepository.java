package com.example.controlasistenciabackend.repository;

import com.example.controlasistenciabackend.entity.Asistencia;
import com.example.controlasistenciabackend.entity.TipoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // Lista asistencias de un empleado específico, ordenadas por fecha descendente
    List<Asistencia> findByEmpleadoIdOrderByFechaHoraDesc(Long empleadoId);

    // Lista todas las asistencias ordenadas por fecha descendente
    List<Asistencia> findAllByOrderByFechaHoraDesc();

    // Reporte: asistencias en un rango de fechas (opcional filtrar por empleado)
    List<Asistencia> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime desde, LocalDateTime hasta);

    List<Asistencia> findByEmpleadoIdAndFechaHoraBetweenOrderByFechaHoraDesc(
            Long empleadoId, LocalDateTime desde, LocalDateTime hasta);

    // Resumen: conteo de asistencias por tipo en un rango (opcional filtrar por empleado)
    long countByTipoAndFechaHoraBetween(TipoAsistencia tipo, LocalDateTime desde, LocalDateTime hasta);

    long countByTipoAndEmpleadoIdAndFechaHoraBetween(
            TipoAsistencia tipo, Long empleadoId, LocalDateTime desde, LocalDateTime hasta);
}