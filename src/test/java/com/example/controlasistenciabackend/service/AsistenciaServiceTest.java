package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.AsistenciaRequest;
import com.example.controlasistenciabackend.dto.AsistenciaResponse;
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

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    @Test
    void registrarAsistencia_conEmpleadoInexistente_lanzaNoSuchElement() {
        Long id = 999L;
        when(empleadoRepository.findById(id)).thenReturn(Optional.empty());

        AsistenciaRequest request = new AsistenciaRequest(id, TipoAsistencia.ENTRADA);

        assertThrows(NoSuchElementException.class, () -> asistenciaService.registrarAsistencia(request));
        verify(asistenciaRepository, never()).save(any());
    }

    @Test
    void registrarAsistencia_entrada_persisteYDevuelveTipo() {
        Empleado empleado = new Empleado();
        empleado.setNombre("Ana");
        setId(empleado, 1L);
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));

        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(inv -> {
            Asistencia a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AsistenciaRequest request = new AsistenciaRequest(1L, TipoAsistencia.ENTRADA);
        AsistenciaResponse response = asistenciaService.registrarAsistencia(request);

        assertEquals(1L, response.empleadoId());
        assertEquals(TipoAsistencia.ENTRADA, response.tipo());
        assertNotNull(response.fechaHora());
        verify(asistenciaRepository).save(any(Asistencia.class));
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