package com.example.controlasistenciabackend;

import com.example.controlasistenciabackend.entity.Asistencia;
import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.entity.Role;
import com.example.controlasistenciabackend.entity.TipoAsistencia;
import com.example.controlasistenciabackend.entity.Usuario;
import com.example.controlasistenciabackend.repository.AsistenciaRepository;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;
import com.example.controlasistenciabackend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        crearAdministrador();
        cargarDatosDemostracion();
    }

    /**
     * Crea o actualiza el usuario administrador por defecto.
     */
    private void crearAdministrador() {
        usuarioRepository.findByUsername(adminUsername).ifPresentOrElse(
                adminExistente -> {
                    // Actualizar la contraseña si cambió la variable de entorno
                    if (!passwordEncoder.matches(adminPassword, adminExistente.getPassword())) {
                        adminExistente.setPassword(passwordEncoder.encode(adminPassword));
                        adminExistente.setRole(Role.ADMIN);
                        adminExistente.setActivo(true);
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
                            .activo(true)
                            .build();
                    usuarioRepository.save(admin);
                    log.info("Usuario administrador por defecto creado: {}", adminUsername);
                }
        );
    }

    /**
     * Carga datos de demostración (empleados y asistencias de prueba) únicamente
     * con fines de presentación y elaboración del manual de usuario.
     *
     * - Si no existen empleados, se crean empleados ficticios de demostración.
     * - Si no existen asistencias de demostración, se crean registros de prueba
     *   asociados a los empleados disponibles (reales o ficticios).
     *
     * Los registros de demostración se identifican internamente con el campo
     * {@code demo = true} y no afectan los datos reales existentes.
     */
    private void cargarDatosDemostracion() {
        // 1. Asegurar empleados de demostración si no hay ninguno en el sistema
        List<Empleado> empleados = empleadoRepository.findAll();
        if (empleados.isEmpty()) {
            empleados = crearEmpleadosDemostracion();
        }

        // 2. Crear asistencias de demostración solo si aún no existen registros demo
        boolean existenDemo = asistenciaRepository.findAll().stream()
                .anyMatch(Asistencia::isDemo);
        if (!existenDemo) {
            crearAsistenciasDemostracion(empleados);
        } else {
            log.info("Ya existen asistencias de demostración en el sistema.");
        }
    }

    /**
     * Crea empleados ficticios de demostración cuando el sistema no tiene ninguno.
     */
    private List<Empleado> crearEmpleadosDemostracion() {
        List<Empleado> empleadosDemo = new ArrayList<>();

        Empleado juan = new Empleado();
        juan.setNombre("Juan Pérez");
        juan.setDocumento("1000000001");
        juan.setCargo("Desarrollador");
        empleadosDemo.add(empleadoRepository.save(juan));

        Empleado maria = new Empleado();
        maria.setNombre("María Gómez");
        maria.setDocumento("1000000002");
        maria.setCargo("Analista");
        empleadosDemo.add(empleadoRepository.save(maria));

        Empleado carlos = new Empleado();
        carlos.setNombre("Carlos Ruiz");
        carlos.setDocumento("1000000003");
        carlos.setCargo("Diseñador");
        empleadosDemo.add(empleadoRepository.save(carlos));

        Empleado ana = new Empleado();
        ana.setNombre("Ana Torres");
        ana.setDocumento("1000000004");
        ana.setCargo("QA");
        empleadosDemo.add(empleadoRepository.save(ana));

        log.info("Empleados de demostración creados: {}", empleadosDemo.size());
        return empleadosDemo;
    }

    /**
     * Crea asistencias de demostración distribuidas entre los empleados disponibles
     * y en diferentes fechas/horarios, para que el historial se vea realista.
     */
    private void crearAsistenciasDemostracion(List<Empleado> empleados) {
        if (empleados.isEmpty()) {
            log.warn("No hay empleados disponibles para crear asistencias de demostración.");
            return;
        }

        // Definición de registros de prueba: (índice de empleado, fecha/hora, tipo)
        // Se usa el índice módulo del tamaño de la lista para repartir entre los empleados.
        Object[][] registros = {
                {0, LocalDateTime.of(2026, 8, 14, 7, 58), TipoAsistencia.ENTRADA},
                {1, LocalDateTime.of(2026, 8, 14, 8, 5), TipoAsistencia.ENTRADA},
                {2, LocalDateTime.of(2026, 8, 14, 8, 12), TipoAsistencia.ENTRADA},
                {3, LocalDateTime.of(2026, 8, 14, 8, 27), TipoAsistencia.ENTRADA},
                {0, LocalDateTime.of(2026, 8, 14, 17, 2), TipoAsistencia.SALIDA},
                {1, LocalDateTime.of(2026, 8, 13, 8, 3), TipoAsistencia.ENTRADA},
                {2, LocalDateTime.of(2026, 8, 13, 8, 41), TipoAsistencia.ENTRADA},
                {3, LocalDateTime.of(2026, 8, 13, 9, 3), TipoAsistencia.ENTRADA},
                {0, LocalDateTime.of(2026, 8, 13, 17, 15), TipoAsistencia.SALIDA},
                {1, LocalDateTime.of(2026, 8, 12, 7, 55), TipoAsistencia.ENTRADA},
                {2, LocalDateTime.of(2026, 8, 12, 8, 18), TipoAsistencia.ENTRADA},
                {3, LocalDateTime.of(2026, 8, 12, 8, 33), TipoAsistencia.ENTRADA},
        };

        int contador = 0;
        for (Object[] registro : registros) {
            int indiceEmpleado = (int) registro[0] % empleados.size();
            Empleado empleado = empleados.get(indiceEmpleado);
            LocalDateTime fechaHora = (LocalDateTime) registro[1];
            TipoAsistencia tipo = (TipoAsistencia) registro[2];

            Asistencia asistencia = Asistencia.builder()
                    .empleadoId(empleado.getId())
                    .fechaHora(fechaHora)
                    .tipo(tipo)
                    .demo(true)
                    .build();

            asistenciaRepository.save(asistencia);
            contador++;
        }

        log.info("Asistencias de demostración creadas: {}", contador);
    }
}