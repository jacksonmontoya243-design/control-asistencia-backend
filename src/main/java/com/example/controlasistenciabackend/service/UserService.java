package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.dto.UpdateRoleRequest;
import com.example.controlasistenciabackend.dto.UserResponse;
import com.example.controlasistenciabackend.entity.Role;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio de gestión de usuarios: listar, consultar, cambiar rol y eliminar.
 * Usa inyección por constructor (mejora sobre @Autowired en campos).
 */
@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;

    public UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

    public UserResponse cambiarRol(Long id, UpdateRoleRequest request) {
        if (request.role() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        usuario.setRole(request.role());
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
        return new UserResponse(usuario.getId(), usuario.getUsername(), usuario.getRole());
    }
}
