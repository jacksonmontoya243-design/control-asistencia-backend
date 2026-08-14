package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.AsistenciaConsultaResponse;
import com.example.controlasistenciabackend.dto.AsistenciaRequest;
import com.example.controlasistenciabackend.dto.AsistenciaResponse;
import com.example.controlasistenciabackend.entity.Asistencia;
import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.entity.TipoAsistencia;
import com.example.controlasistenciabackend.repository.AsistenciaRepository;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Servicio de asistencias: registro de entradas/salidas, consultas y reportes.
 */
@Service
public class AsistenciaService {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaService.class);

    private final AsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository empleadoRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository,
                             EmpleadoRepository empleadoRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Registra una asistencia (entrada o salida) para un empleado.
     * Valida que el empleado exista antes de registrar.
     */
    public AsistenciaResponse registrarAsistencia(AsistenciaRequest request) {
        // Validar que el empleado exista
        Empleado empleado = empleadoRepository.findById(request.empleadoId())
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado con ID: " + request.empleadoId()));

        Asistencia asistencia = Asistencia.builder()
                .empleadoId(empleado.getId())
                .fechaHora(LocalDateTime.now())
                .tipo(request.tipo())
                .build();

        Asistencia guardada = asistenciaRepository.save(asistencia);

        return toResponse(guardada);
    }

    /**
     * Lista todas las asistencias ordenadas por fecha descendente.
     */
    public List<AsistenciaResponse> listarTodas() {
        return asistenciaRepository.findAllByOrderByFechaHoraDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lista asistencias de un empleado específico.
     */
    public List<AsistenciaResponse> listarPorEmpleado(Long empleadoId) {
        return asistenciaRepository.findByEmpleadoIdOrderByFechaHoraDesc(empleadoId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Reporte de asistencias en un rango de fechas, opcionalmente filtrado por empleado.
     * Si desde/hasta son null, se devuelven todas (sin filtro de rango).
     */
    public List<AsistenciaResponse> reporte(Long empleadoId, LocalDateTime desde, LocalDateTime hasta) {
        List<Asistencia> resultado;

        if (empleadoId != null && desde != null && hasta != null) {
            resultado = asistenciaRepository.findByEmpleadoIdAndFechaHoraBetweenOrderByFechaHoraDesc(empleadoId, desde, hasta);
        } else if (empleadoId != null) {
            resultado = asistenciaRepository.findByEmpleadoIdOrderByFechaHoraDesc(empleadoId);
        } else if (desde != null && hasta != null) {
            resultado = asistenciaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(desde, hasta);
        } else {
            resultado = asistenciaRepository.findAllByOrderByFechaHoraDesc();
        }

        return resultado.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Resumen (conteo de ENTRADAS y SALIDAS) en un rango de fechas, opcional por empleado.
     * Si desde/hasta son null, se usa un rango por defecto (desde el inicio de los tiempos hasta ahora).
     */
    public Map<String, Long> resumen(Long empleadoId, LocalDateTime desde, LocalDateTime hasta) {
        Map<String, Long> resumen = new HashMap<>();

        // Si no se especifica rango, contar todas las asistencias del tipo
        if (desde == null && hasta == null) {
            if (empleadoId != null) {
                resumen.put("entradas", asistenciaRepository.findByEmpleadoIdOrderByFechaHoraDesc(empleadoId)
                        .stream().filter(a -> a.getTipo() == TipoAsistencia.ENTRADA).count());
                resumen.put("salidas", asistenciaRepository.findByEmpleadoIdOrderByFechaHoraDesc(empleadoId)
                        .stream().filter(a -> a.getTipo() == TipoAsistencia.SALIDA).count());
            } else {
                resumen.put("entradas", asistenciaRepository.findAllByOrderByFechaHoraDesc()
                        .stream().filter(a -> a.getTipo() == TipoAsistencia.ENTRADA).count());
                resumen.put("salidas", asistenciaRepository.findAllByOrderByFechaHoraDesc()
                        .stream().filter(a -> a.getTipo() == TipoAsistencia.SALIDA).count());
            }
        } else if (empleadoId != null) {
            resumen.put("entradas", asistenciaRepository.countByTipoAndEmpleadoIdAndFechaHoraBetween(
                    TipoAsistencia.ENTRADA, empleadoId, desde, hasta));
            resumen.put("salidas", asistenciaRepository.countByTipoAndEmpleadoIdAndFechaHoraBetween(
                    TipoAsistencia.SALIDA, empleadoId, desde, hasta));
        } else {
            resumen.put("entradas", asistenciaRepository.countByTipoAndFechaHoraBetween(
                    TipoAsistencia.ENTRADA, desde, hasta));
            resumen.put("salidas", asistenciaRepository.countByTipoAndFechaHoraBetween(
                    TipoAsistencia.SALIDA, desde, hasta));
        }

        log.debug("Resumen de asistencias generado: {}", resumen);
        return resumen;
    }

    /**
     * Consulta de asistencias con filtros opcionales y datos del empleado cruzados.
     * <p>
     * Filtros disponibles (todos opcionales):
     * <ul>
     *     <li>{@code termino}: busca por nombre o documento del empleado (case-insensitive).</li>
     *     <li>{@code tipo}: filtra por ENTRADA/SALIDA.</li>
     *     <li>{@code desde}/{@code hasta}: rango de fechas.</li>
     * </ul>
     * El cruce con la tabla de empleados se realiza en memoria (carga en lote) porque
     * la entidad {@link Asistencia} no mantiene una relación JPA con {@link Empleado}.
     */
    public List<AsistenciaConsultaResponse> consultarAsistencias(String termino, TipoAsistencia tipo,
                                                                 LocalDateTime desde, LocalDateTime hasta) {
        List<Asistencia> asistencias = filtrarAsistencias(tipo, desde, hasta);

        // Carga en lote de todos los empleados referenciados por las asistencias
        Set<Long> empleadoIds = asistencias.stream()
                .map(Asistencia::getEmpleadoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Empleado> empleadosPorId = empleadoIds.isEmpty()
                ? Map.of()
                : empleadoRepository.findAllById(empleadoIds).stream()
                        .collect(Collectors.toMap(Empleado::getId, Function.identity()));

        String patron = termino == null ? "" : termino.trim().toLowerCase();

        return asistencias.stream()
                .filter(a -> coincidirTermino(a, empleadosPorId, patron))
                .map(a -> toConsultaResponse(a, empleadosPorId))
                .collect(Collectors.toList());
    }

    /**
     * Aplica los filtros de tipo y rango de fechas a nivel de repositorio.
     */
    private List<Asistencia> filtrarAsistencias(TipoAsistencia tipo, LocalDateTime desde, LocalDateTime hasta) {
        boolean hayTipo = tipo != null;
        boolean hayRango = desde != null && hasta != null;

        if (hayTipo && hayRango) {
            return asistenciaRepository.findByTipoAndFechaHoraBetweenOrderByFechaHoraDesc(tipo, desde, hasta);
        }
        if (hayTipo) {
            return asistenciaRepository.findByTipoOrderByFechaHoraDesc(tipo);
        }
        if (hayRango) {
            return asistenciaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(desde, hasta);
        }
        return asistenciaRepository.findAllByOrderByFechaHoraDesc();
    }

    /**
     * Verifica si una asistencia coincide con el término de búsqueda (nombre/documento del empleado).
     */
    private boolean coincidirTermino(Asistencia asistencia, Map<Long, Empleado> empleadosPorId, String patron) {
        if (patron.isEmpty()) {
            return true;
        }
        Empleado empleado = empleadosPorId.get(asistencia.getEmpleadoId());
        if (empleado == null) {
            return false;
        }
        String nombre = empleado.getNombre();
        String documento = empleado.getDocumento();
        return (nombre != null && nombre.toLowerCase().contains(patron))
                || (documento != null && documento.toLowerCase().contains(patron));
    }

    /**
     * Mapea una asistencia a {@link AsistenciaConsultaResponse} resolviendo los datos del empleado.
     * Si el empleado ya no existe, los campos de empleado se devuelven como null.
     */
    private AsistenciaConsultaResponse toConsultaResponse(Asistencia asistencia, Map<Long, Empleado> empleadosPorId) {
        Empleado empleado = empleadosPorId.get(asistencia.getEmpleadoId());
        return new AsistenciaConsultaResponse(
                asistencia.getId(),
                asistencia.getEmpleadoId(),
                empleado != null ? empleado.getNombre() : null,
                empleado != null ? empleado.getDocumento() : null,
                empleado != null ? empleado.getCargo() : null,
                asistencia.getFechaHora(),
                asistencia.getTipo(),
                asistencia.isDemo()
        );
    }

    private AsistenciaResponse toResponse(Asistencia asistencia) {
        return new AsistenciaResponse(
                asistencia.getId(),
                asistencia.getEmpleadoId(),
                asistencia.getFechaHora(),
                asistencia.getTipo()
        );
    }
}