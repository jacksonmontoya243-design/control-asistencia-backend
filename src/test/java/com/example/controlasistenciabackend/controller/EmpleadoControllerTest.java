package com.example.controlasistenciabackend.controller;

import com.example.controlasistenciabackend.SecurityConfig;
import com.example.controlasistenciabackend.dto.EmpleadoRequest;
import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import com.example.controlasistenciabackend.security.JwtAuthenticationFilter;
import com.example.controlasistenciabackend.service.EmpleadoService;
import com.example.controlasistenciabackend.service.QrGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmpleadoController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmpleadoRepository repository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private EmpleadoService empleadoService;

    @MockitoBean
    private QrGeneratorService qrGeneratorService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    void guardar_conBodyVacio_devuelve400ConErrores() throws Exception {
        String body = objectMapper.writeValueAsString(new EmpleadoRequest("", "", ""));

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error de validación de datos"));

        verify(repository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void guardar_conDatosValidos_guardaYGeneraQR() throws Exception {
        Empleado nuevo = new Empleado();
        nuevo.setNombre("Ana");
        nuevo.setDocumento("123");
        nuevo.setCargo("Empleado");
        new EntityIdSetter().asignar(nuevo, 5L);

        when(empleadoService.guardarEmpleado(any(Empleado.class))).thenReturn(nuevo);

        String body = objectMapper.writeValueAsString(new EmpleadoRequest("Ana", "123", "Empleado"));

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Ana"));

        verify(qrGeneratorService).generarQR(anyString(), eq("empleado_5"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_conIdInexistente_devuelve404() throws Exception {
        when(empleadoService.actualizarEmpleado(eq(99L), any(Empleado.class))).thenReturn(null);

        String body = objectMapper.writeValueAsString(new EmpleadoRequest("Ana", "123", "Empleado"));

        mockMvc.perform(put("/api/empleados/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_conIdInexistente_devuelve404() throws Exception {
        when(repository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/empleados/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_sinAutenticacion_devuelve401() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_devuelvePagina() throws Exception {
        Empleado e = new Empleado();
        e.setNombre("Ana");
        e.setDocumento("123");
        e.setCargo("Empleado");
        when(empleadoService.buscarEmpleados(null, 0, 10))
                .thenReturn(new PageImpl<>(List.of(e)));

        mockMvc.perform(get("/api/empleados?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nombre").value("Ana"));
    }

    static class EntityIdSetter {
        void asignar(Empleado empleado, long id) {
            // Acceso al id sin setter público: se usa reflexión solo para el test.
            try {
                var field = Empleado.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(empleado, id);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}