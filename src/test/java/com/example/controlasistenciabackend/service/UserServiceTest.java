package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.UpdateRoleRequest;
import com.example.controlasistenciabackend.dto.UserResponse;
import com.example.controlasistenciabackend.entity.Role;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void obtenerUsuario_conIdInexistente_lanzaNoSuchElement() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.obtenerUsuario(99L));
    }

    @Test
    void cambiarRol_sinRol_lanzaIllegalArgument() {
        UpdateRoleRequest request = new UpdateRoleRequest(null);

        assertThrows(IllegalArgumentException.class, () -> userService.cambiarRol(1L, request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cambiarRol_conDatosValidos_actualizaRol() {
        Usuario usuario = Usuario.builder().id(1L).username("ana").role(Role.EMPLEADO).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.cambiarRol(1L, new UpdateRoleRequest(Role.SUPERVISOR));

        assertEquals(Role.SUPERVISOR, response.role());
        assertEquals(Role.SUPERVISOR, usuario.getRole());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void eliminarUsuario_eliminandoAlMismoUsuario_lanzaIllegalState() {
        Usuario usuario = Usuario.builder().id(1L).username("admin").role(Role.ADMIN).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        assertThrows(IllegalStateException.class, () -> userService.eliminarUsuario(1L));
        verify(usuarioRepository, never()).delete(any());
        SecurityContextHolder.clearContext();
    }

    @Test
    void eliminarUsuario_deOtroUsuario_elimina() {
        Usuario usuario = Usuario.builder().id(1L).username("ana").role(Role.EMPLEADO).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        userService.eliminarUsuario(1L);
        verify(usuarioRepository).delete(usuario);
        SecurityContextHolder.clearContext();
    }
}