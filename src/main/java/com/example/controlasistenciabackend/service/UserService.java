package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.CreateUserRequest;
import com.example.controlasistenciabackend.dto.UpdateActivoRequest;
import com.example.controlasistenciabackend.dto.UpdatePasswordRequest;
import com.example.controlasistenciabackend.dto.UpdateRoleRequest;
import com.example.controlasistenciabackend.dto.UserResponse;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio de gestión de usuarios: listar, consultar, crear, cambiar rol,
 * cambiar contraseña, activar/desactivar y eliminar.
 * Usa inyección por constructor (mejora sobre @Autowired en campos).
 */
@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return toResponse(usuario);
    }

    /**
     * Crea un nuevo usuario con contraseña encriptada.
     * El administrador puede asignar rol y asociar un empleado.
     */
    public UserResponse crearUsuario(CreateUserRequest request) {
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .activo(true)
                .empleadoId(request.empleadoId())
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    public UserResponse cambiarRol(Long id, UpdateRoleRequest request) {
        if (request.role() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        usuario.setRole(request.role());
        return toResponse(usuarioRepository.save(usuario));
    }

    /**
     * Cambia la contraseña de un usuario (reset por parte del administrador).
     * La contraseña se encripta con BCrypt antes de guardarse.
     */
    public UserResponse cambiarPassword(Long id, UpdatePasswordRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(request.password()));
        return toResponse(usuarioRepository.save(usuario));
    }

    /**
     * Activa o desactiva un usuario.
     * No permite desactivar el propio usuario autenticado.
     */
    public UserResponse cambiarActivo(Long id, UpdateActivoRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        String usernameActual = nombreUsuarioAutenticado();

        if (usuario.getUsername().equals(usernameActual) && !request.activo()) {
            throw new IllegalStateException("No puedes desactivar tu propio usuario");
        }

        usuario.setActivo(request.activo());
        return toResponse(usuarioRepository.save(usuario));
    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        String usernameActual = nombreUsuarioAutenticado();

        if (usuario.getUsername().equals(usernameActual)) {
            throw new IllegalStateException("No puedes eliminar tu propio usuario");
        }

        usuarioRepository.delete(usuario);
    }

    private String nombreUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private UserResponse toResponse(Usuario usuario) {
        return new UserResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRole(),
                usuario.isActivo(),
                usuario.getEmpleadoId()
        );
    }
}