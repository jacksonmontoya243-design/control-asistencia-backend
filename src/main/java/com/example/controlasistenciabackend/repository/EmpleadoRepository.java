package com.example.controlasistenciabackend.repository;

import com.example.controlasistenciabackend.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    // Este método permite filtrar por cargo
    long countByCargo(String cargo);

    // Búsqueda por nombre, documento o cargo (case-insensitive), paginada
    Page<Empleado> findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCaseOrCargoContainingIgnoreCase(
            String nombre,
            String documento,
            String cargo,
            Pageable pageable
    );

    // Conteo de empleados que coinciden con el término de búsqueda
    long countByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCaseOrCargoContainingIgnoreCase(
            String nombre,
            String documento,
            String cargo
    );
}