package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.AuthResponse;
import com.example.controlasistenciabackend.dto.LoginRequest;
import com.example.controlasistenciabackend.dto.RegisterRequest;
import com.example.controlasistenciabackend.entity.Role;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import com.example.controlasistenciabackend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrarUsuario_conUsernameExistente_lanzaIllegalArgumentException() {
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(new Usuario()));
        RegisterRequest request = new RegisterRequest("admin", "123456", Role.ADMIN);

        assertThrows(IllegalArgumentException.class, () -> authService.registrarUsuario(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarUsuario_conDatosValidos_encriptaPasswordYAsignaRol() {
        when(usuarioRepository.findByUsername("nuevo")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterRequest request = new RegisterRequest("nuevo", "123456", Role.SUPERVISOR);
        Usuario resultado = authService.registrarUsuario(request);

        assertEquals("nuevo", resultado.getUsername());
        assertEquals("hash", resultado.getPassword());
        assertEquals(Role.EMPLEADO, resultado.getRole());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_sinRol_asignaEMPLEADOPorDefecto() {
        when(usuarioRepository.findByUsername("emp")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterRequest request = new RegisterRequest("emp", "123456", null);
        Usuario resultado = authService.registrarUsuario(request);

        assertEquals(Role.EMPLEADO, resultado.getRole());
    }

    @Test
    void login_conUsernameInexistente_lanzaBadCredentials() {
        when(usuarioRepository.findByUsername("nope")).thenReturn(Optional.empty());
        LoginRequest request = new LoginRequest("nope", "123456");

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_conPasswordErronea_lanzaBadCredentials() {
        Usuario usuario = Usuario.builder()
                .username("admin")
                .password("hash")
                .role(Role.ADMIN)
                .build();
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "hash")).thenReturn(false);

        LoginRequest request = new LoginRequest("admin", "incorrecta");
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_conCredencialesValidas_devuelveToken() {
        Usuario usuario = Usuario.builder()
                .username("admin")
                .password("hash")
                .role(Role.ADMIN)
                .build();
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secreta", "hash")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("token-jwt");

        LoginRequest request = new LoginRequest("admin", "secreta");
        AuthResponse response = authService.login(request);

        assertEquals("token-jwt", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals("admin", response.username());
        assertEquals(Role.ADMIN, response.role());
    }
}