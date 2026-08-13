package com.example.controlasistenciabackend.controller;

import com.example.controlasistenciabackend.dto.CreateUserRequest;
import com.example.controlasistenciabackend.dto.UpdateActivoRequest;
import com.example.controlasistenciabackend.dto.UpdatePasswordRequest;
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

    @PostMapping
    public UserResponse crear(@Valid @RequestBody CreateUserRequest request) {
        return userService.crearUsuario(request);
    }

    @PutMapping("/{id}/role")
    public UserResponse cambiarRol(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return userService.cambiarRol(id, request);
    }

    @PutMapping("/{id}/password")
    public UserResponse cambiarPassword(@PathVariable Long id, @Valid @RequestBody UpdatePasswordRequest request) {
        return userService.cambiarPassword(id, request);
    }

    @PutMapping("/{id}/activo")
    public UserResponse cambiarActivo(@PathVariable Long id, @Valid @RequestBody UpdateActivoRequest request) {
        return userService.cambiarActivo(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}