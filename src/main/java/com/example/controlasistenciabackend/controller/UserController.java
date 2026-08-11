package com.example.controlasistenciabackend.controller;

import com.example.controlasistenciabackend.dto.UpdateRoleRequest;
import com.example.controlasistenciabackend.dto.UserResponse;
import com.example.controlasistenciabackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de gestión de usuarios. Solo accesible para ADMIN.
 */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> listar() {
        return userService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public UserResponse obtener(@PathVariable Long id) {
        return userService.obtenerUsuario(id);
    }

    @PutMapping("/{id}/role")
    public UserResponse cambiarRol(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return userService.cambiarRol(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}