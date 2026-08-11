package com.example.controlasistenciabackend.controller;

import com.example.controlasistenciabackend.dto.EmpleadoRequest;
import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.entity.Role;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import com.example.controlasistenciabackend.service.EmpleadoService;
import com.example.controlasistenciabackend.service.QrGeneratorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private QrGeneratorService qrGeneratorService;

    @Value("${app.qr.base-url:http://localhost:8080}")
    private String qrBaseUrl;

    /**
     * Lista empleados paginada con búsqueda opcional.
     * GET /api/empleados?termino=ana&page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public Page<Empleado> listar(
            @RequestParam(required = false) String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return empleadoService.buscarEmpleados(termino, page, size);
    }

    /**
     * Lista completa de empleados (sin paginar) para compatibilidad.
     * GET /api/empleados/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public List<Empleado> listarTodos() {
        return empleadoService.obtenerTodos();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Empleado guardar(@Valid @RequestBody EmpleadoRequest request) {
        Empleado empleado = new Empleado();
        empleado.setNombre(request.nombre());
        empleado.setDocumento(request.documento());
        empleado.setCargo(request.cargo());
        Empleado nuevoEmpleado = empleadoService.guardarEmpleado(empleado);
        String datosQR = qrBaseUrl + "/empleado/" + nuevoEmpleado.getId();
        qrGeneratorService.generarQR(datosQR, "empleado_" + nuevoEmpleado.getId());
        return nuevoEmpleado;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empleado> actualizar(@PathVariable Long id, @Valid @RequestBody EmpleadoRequest request) {
        Empleado empleado = new Empleado();
        empleado.setNombre(request.nombre());
        empleado.setDocumento(request.documento());
        empleado.setCargo(request.cargo());
        Empleado actualizado = empleadoService.actualizarEmpleado(id, empleado);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            empleadoService.eliminarEmpleado(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public long obtenerTotalEmpleados() {
        return repository.count();
    }

    @GetMapping("/count-stats")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public Map<String, Long> obtenerEstadisticas() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("empleados", repository.count());
        stats.put("admins", usuarioRepository.countByRole(Role.ADMIN));
        stats.put("supervisores", usuarioRepository.countByRole(Role.SUPERVISOR));
        return stats;
    }
}