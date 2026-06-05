package com.example.controlasistenciabackend.repository;

import com.example.controlasistenciabackend.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}