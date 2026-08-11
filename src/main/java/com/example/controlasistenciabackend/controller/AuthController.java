package com.example.controlasistenciabackend.controller;

import com.example.controlasistenciabackend.dto.AuthResponse;
import com.example.controlasistenciabackend.dto.LoginRequest;
import com.example.controlasistenciabackend.dto.RegisterRequest;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = authService.registrarUsuario(request);
        return ResponseEntity.ok("Usuario registrado exitosamente: " + usuario.getUsername() + " (rol: " + usuario.getRole() + ")");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}