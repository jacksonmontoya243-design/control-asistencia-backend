package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.AsistenciaConsultaResponse;
import com.example.controlasistenciabackend.entity.Asistencia;
import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.entity.TipoAsistencia;
import com.example.controlasistenciabackend.repository.AsistenciaRepository;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsistenciaConsultaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private Empleado empleado(long id, String nombre, String documento, String cargo) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setDocumento(documento);
        e.setCargo(cargo);
        setId(e, id);
        return e;
    }

    @Test
    void consultar_sinFiltros_devuelveTodasConDatosDelEmpleado() {
        Empleado juan = empleado(1L, "Juan Pérez", "1000000001", "Desarrollador");
        Asistencia entrada = Asistencia.builder()
                .id(10L).empleadoId(1L)
                .fechaHora(LocalDateTime.of(2026, 8, 14, 7, 58))
                .tipo(TipoAsistencia.ENTRADA).build();

        when(asistenciaRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(entrada));
        when(empleadoRepository.findAllById(anyCollection())).thenReturn(List.of(juan));

        List<AsistenciaConsultaResponse> resultado = asistenciaService.consultarAsistencias(null, null, null, null);

        assertEquals(1, resultado.size());
        AsistenciaConsultaResponse r = resultado.get(0);
        assertEquals("Juan Pérez", r.nombreEmpleado());
        assertEquals("1000000001", r.documento());
        assertEquals("Desarrollador", r.cargo());
        assertEquals(TipoAsistencia.ENTRADA, r.tipo());
        assertFalse(r.demo());
    }

    @Test
    void consultar_porTipo_filtraEnRepositorio() {
        Empleado maria = empleado(2L, "María Gómez", "1000000002", "Analista");
        Asistencia salida = Asistencia.builder()
                .id(11L).empleadoId(2L)
                .fechaHora(LocalDateTime.of(2026, 8, 14, 17, 2))
                .tipo(TipoAsistencia.SALIDA).build();

        when(asistenciaRepository.findByTipoOrderByFechaHoraDesc(TipoAsistencia.SALIDA))
                .thenReturn(List.of(salida));
        when(empleadoRepository.findAllById(anyCollection())).thenReturn(List.of(maria));

        List<AsistenciaConsultaResponse> resultado =
                asistenciaService.consultarAsistencias(null, TipoAsistencia.SALIDA, null, null);

        assertEquals(1, resultado.size());
        assertEquals(TipoAsistencia.SALIDA, resultado.get(0).tipo());
    }

    @Test
    void consultar_porRango_filtraEnRepositorio() {
        Empleado carlos = empleado(3L, "Carlos Ruiz", "1000000003", "Diseñador");
        Asistencia entrada = Asistencia.builder()
                .id(12L).empleadoId(3L)
                .fechaHora(LocalDateTime.of(2026, 8, 12, 8, 18))
                .tipo(TipoAsistencia.ENTRADA).build();

        LocalDateTime desde = LocalDateTime.of(2026, 8, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2026, 8, 31, 23, 59);
        when(asistenciaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(desde, hasta))
                .thenReturn(List.of(entrada));
        when(empleadoRepository.findAllById(anyCollection())).thenReturn(List.of(carlos));

        List<AsistenciaConsultaResponse> resultado =
                asistenciaService.consultarAsistencias(null, null, desde, hasta);

        assertEquals(1, resultado.size());
        assertEquals("Carlos Ruiz", resultado.get(0).nombreEmpleado());
    }

    @Test
    void consultar_porTermino_filtraPorNombreDelEmpleado() {
        Empleado juan = empleado(1L, "Juan Pérez", "1000000001", "Desarrollador");
        Empleado maria = empleado(2L, "María Gómez", "1000000002", "Analista");

        Asistencia a1 = Asistencia.builder()
                .id(1L).empleadoId(1L)
                .fechaHora(LocalDateTime.of(2026, 8, 14, 7, 58))
                .tipo(TipoAsistencia.ENTRADA).build();
        Asistencia a2 = Asistencia.builder()
                .id(2L).empleadoId(2L)
                .fechaHora(LocalDateTime.of(2026, 8, 14, 8, 5))
                .tipo(TipoAsistencia.ENTRADA).build();

        when(asistenciaRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(a1, a2));
        when(empleadoRepository.findAllById(anyCollection())).thenReturn(List.of(juan, maria));

        List<AsistenciaConsultaResponse> resultado =
                asistenciaService.consultarAsistencias("juan", null, null, null);

        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).nombreEmpleado());
    }

    @Test
    void consultar_porTermino_filtraPorDocumentoDelEmpleado() {
        Empleado juan = empleado(1L, "Juan Pérez", "1000000001", "Desarrollador");

        Asistencia a1 = Asistencia.builder()
                .id(1L).empleadoId(1L)
                .fechaHora(LocalDateTime.of(2026, 8, 14, 7, 58))
                .tipo(TipoAsistencia.ENTRADA).build();

        when(asistenciaRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(a1));
        when(empleadoRepository.findAllById(anyCollection())).thenReturn(List.of(juan));

        List<AsistenciaConsultaResponse> resultado =
                asistenciaService.consultarAsistencias("0000001", null, null, null);

        assertEquals(1, resultado.size());
        assertEquals("1000000001", resultado.get(0).documento());
    }

    @Test
    void consultar_empleadoInexistente_noRompeYDevuelveCamposNull() {
        Asistencia a1 = Asistencia.builder()
                .id(99L).empleadoId(999L)
                .fechaHora(LocalDateTime.of(2026, 8, 14, 8, 0))
                .tipo(TipoAsistencia.ENTRADA).build();

        when(asistenciaRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(a1));
        when(empleadoRepository.findAllById(anyCollection())).thenReturn(List.of());

        List<AsistenciaConsultaResponse> resultado =
                asistenciaService.consultarAsistencias(null, null, null, null);

        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).nombreEmpleado());
        assertNull(resultado.get(0).documento());
        assertEquals(999L, resultado.get(0).empleadoId());
    }

    private void setId(Empleado empleado, long id) {
        try {
            var field = Empleado.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(empleado, id);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
