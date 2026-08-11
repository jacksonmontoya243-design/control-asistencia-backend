package com.example.controlasistenciabackend;

import com.example.controlasistenciabackend.entity.Role;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        usuarioRepository.findByUsername(adminUsername).ifPresentOrElse(
                adminExistente -> {
                    // Actualizar la contraseña si cambió la variable de entorno
                    if (!passwordEncoder.matches(adminPassword, adminExistente.getPassword())) {
                        adminExistente.setPassword(passwordEncoder.encode(adminPassword));
                        adminExistente.setRole(Role.ADMIN);
                        usuarioRepository.save(adminExistente);
                        log.info("Credenciales del administrador actualizadas: {}", adminUsername);
                    } else {
                        log.info("El usuario administrador ya existe: {}", adminUsername);
                    }
                },
                () -> {
                    Usuario admin = Usuario.builder()
                            .username(adminUsername)
                            .password(passwordEncoder.encode(adminPassword))
                            .role(Role.ADMIN)
                            .build();
                    usuarioRepository.save(admin);
                    log.info("Usuario administrador por defecto creado: {}", adminUsername);
                }
        );
    }
}