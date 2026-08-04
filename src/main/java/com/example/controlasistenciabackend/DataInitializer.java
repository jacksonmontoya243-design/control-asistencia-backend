package com.example.controlasistenciabackend;

import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            // Contraseña encriptada con BCrypt
            admin.setPassword(passwordEncoder.encode("admin"));
            usuarioRepository.save(admin);
            System.out.println("DEBUG: Usuario por defecto creado");
        } else {
            System.out.println("DEBUG: El usuario admin ya existe.");
        }
    }
}