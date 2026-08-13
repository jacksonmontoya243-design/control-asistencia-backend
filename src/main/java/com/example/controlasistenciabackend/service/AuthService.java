package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.AuthResponse;
import com.example.controlasistenciabackend.dto.LoginRequest;
import com.example.controlasistenciabackend.dto.RegisterRequest;
import com.example.controlasistenciabackend.entity.Role;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import com.example.controlasistenciabackend.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación: registro de usuarios y login con emisión de JWT.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario con contraseña encriptada.
     * El rol SIEMPRE se asigna como EMPLEADO para evitar escalada de privilegios.
     * Los roles superiores solo pueden asignarse mediante el endpoint de administración.
     */
    public Usuario registrarUsuario(RegisterRequest request) {
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.EMPLEADO) // Siempre EMPLEADO, nunca aceptar rol del cliente
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado: {} (rol: {})", guardado.getUsername(), guardado.getRole());
        return guardado;
    }

    /**
     * Valida credenciales y devuelve un AuthResponse con el token JWT.
     * Verifica que el usuario esté activo antes de permitir el acceso.
     */
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        if (!usuario.isActivo()) {
            throw new BadCredentialsException("El usuario está desactivado. Contacta al administrador.");
        }

        String token = jwtService.generarToken(usuario);
        log.info("Login exitoso para el usuario: {}", usuario.getUsername());

        return new AuthResponse(
                token,
                "Bearer",
                jwtExpiration,
                usuario.getUsername(),
                usuario.getRole()
        );
    }
}