package com.example.controlasistenciabackend.controller;

import com.example.controlasistenciabackend.dto.AsistenciaConsultaResponse;
import com.example.controlasistenciabackend.dto.AsistenciaRequest;
import com.example.controlasistenciabackend.dto.AsistenciaResponse;
import com.example.controlasistenciabackend.entity.TipoAsistencia;
import com.example.controlasistenciabackend.service.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    /**
     * Registra una asistencia (entrada o salida).
     * POST /api/asistencias
     */
    @PostMapping
    public ResponseEntity<AsistenciaResponse> registrar(@Valid @RequestBody AsistenciaRequest request) {
        AsistenciaResponse response = asistenciaService.registrarAsistencia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas las asistencias.
     * GET /api/asistencias
     */
    @GetMapping
    public List<AsistenciaResponse> listarTodas() {
        return asistenciaService.listarTodas();
    }

    /**
     * Consulta de asistencias con filtros opcionales y datos del empleado.
     * Solo accesible para ADMIN y SUPERVISOR.
     * GET /api/asistencias/consulta?termino=ana&tipo=ENTRADA&desde=2026-08-01T00:00:00&hasta=2026-08-31T23:59:59
     */
    @GetMapping("/consulta")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public List<AsistenciaConsultaResponse> consultar(
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) TipoAsistencia tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return asistenciaService.consultarAsistencias(termino, tipo, desde, hasta);
    }

    /**
     * Lista asistencias de un empleado específico.
     * GET /api/asistencias/empleado/{id}
     */
    @GetMapping("/empleado/{id}")
    public List<AsistenciaResponse> listarPorEmpleado(@PathVariable Long id) {
        return asistenciaService.listarPorEmpleado(id);
    }

    /**
     * Reporte de asistencias filtrado por empleado y/o rango de fechas.
     * GET /api/asistencias/reporte?empleadoId=1&desde=2026-08-01T00:00:00&hasta=2026-08-31T23:59:59
     */
    @GetMapping("/reporte")
    public List<AsistenciaResponse> reporte(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return asistenciaService.reporte(empleadoId, desde, hasta);
    }

    /**
     * Resumen de entradas/salidas en un rango (opcional por empleado).
     * GET /api/asistencias/resumen?empleadoId=1&desde=...&hasta=...
     */
    @GetMapping("/resumen")
    public Map<String, Long> resumen(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return asistenciaService.resumen(empleadoId, desde, hasta);
    }
}