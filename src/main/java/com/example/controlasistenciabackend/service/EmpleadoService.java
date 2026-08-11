package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * SERVICIO: EmpleadoService
 * Capa de lógica de negocio encargada de gestionar los procesos asociados
 * a los empleados (Crear, Leer, Actualizar y Eliminar).
 * * Se conecta directamente con la capa de persistencia (Repository).
 * * @author Jackson Montoya
 * @version 1.0
 */
@Service
public class EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoService.class);

    private final EmpleadoRepository empleadoRepository;

    /**
     * Inyección de dependencias por constructor.
     */
    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Recupera la lista completa de empleados registrados en el sistema.
     * * @return List<Empleado> Lista con todos los empleados de la base de datos.
     */
    public List<Empleado> obtenerTodos() {
        // Invoca el método findAll() de JPA para traer todos los registros
        List<Empleado> empleados = empleadoRepository.findAll();
        log.debug("Se recuperaron {} empleados", empleados.size());
        return empleados;
    }

    /**
     * Busca empleados paginados por nombre, documento o cargo.
     * @param termino Texto a buscar (si es null o vacío se devuelve todo paginado).
     * @param page Número de página (0-indexado).
     * @param size Tamaño de página.
     * @return Page<Empleado> Página de resultados con metadatos (total, páginas, etc.).
     */
    public Page<Empleado> buscarEmpleados(String termino, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        if (termino == null || termino.isBlank()) {
            return empleadoRepository.findAll(pageable);
        }

        String patron = termino.trim();
        return empleadoRepository
                .findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCaseOrCargoContainingIgnoreCase(
                        patron, patron, patron, pageable
                );
    }

    /**
     * Registra un nuevo empleado en el sistema.
     * * @param empleado Objeto con los datos del colaborador a registrar.
     * @return Empleado El objeto guardado con su respectivo ID generado por la BD.
     */
    public Empleado guardarEmpleado(Empleado empleado) {
        // Persiste los datos usando el método save() de JPA
        Empleado guardado = empleadoRepository.save(empleado);
        log.info("Empleado creado con ID {}", guardado.getId());
        return guardado;
    }

    /**
     * Actualiza la información de un empleado existente buscando por su ID.
     * Estándar aplicado: camelCase para los parámetros del método.
     * * @param id Identificador único del empleado a modificar.
     * @param empleadoActualizado Objeto con los nuevos datos a ingresar.
     * @return Empleado El objeto actualizado y guardado; retorna null si el empleado no existe.
     */
    public Empleado actualizarEmpleado(Long id, Empleado empleadoActualizado) {

        // 1. BUSQUEDA: Intentar encontrar al empleado por su ID. Si no existe, retorna null.
        Empleado empleado = empleadoRepository.findById(id).orElse(null);

        // 2. VALIDACIÓN: Si el empleado existe, se modifican sus atributos
        if (empleado != null) {
            empleado.setNombre(empleadoActualizado.getNombre());
            empleado.setDocumento(empleadoActualizado.getDocumento());
            empleado.setCargo(empleadoActualizado.getCargo());

            // 3. PERSISTENCIA: Se guardan los cambios aplicados sobre el mismo registro
            Empleado actualizado = empleadoRepository.save(empleado);
            log.info("Empleado con ID {} actualizado", id);
            return actualizado;
        }

        log.warn("Intento de actualizar un empleado inexistente con ID {}", id);
        // Retorna null en caso de que el ID proporcionado no coincida con ningún registro
        return null;
    }

    /**
     * Elimina un empleado de la base de datos utilizando su identificador.
     * * @param id Identificador único del empleado que se desea remover.
     * @throws NoSuchElementException si el empleado no existe.
     */
    public void eliminarEmpleado(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new NoSuchElementException("Empleado no encontrado con ID: " + id);
        }
        // Ejecuta la eliminación física del registro en PostgreSQL a través de JPA
        empleadoRepository.deleteById(id);
        log.info("Empleado con ID {} eliminado", id);
    }
}