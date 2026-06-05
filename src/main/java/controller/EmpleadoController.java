package com.example.controlasistenciabackend.controller;

import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.controlasistenciabackend.service.qrgeneratorService;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin("*")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository repository;

    @Autowired
    private qrgeneratorService qrGeneratorService;

    @GetMapping
    public List<Empleado> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Empleado guardar(@RequestBody Empleado empleado) {

        Empleado nuevoEmpleado = repository.save(empleado);

                String datosQR =
                "http://192.168.1.6:8080/empleado/" + nuevoEmpleado.getId();
        qrGeneratorService.generarQR(
                datosQR,
                "empleado_" + nuevoEmpleado.getId()
        );

        return nuevoEmpleado;
    }

    @PutMapping("/{id}")
    public Empleado actualizar(
            @PathVariable Long id,
            @RequestBody Empleado empleado
    ) {

        Empleado existente = repository.findById(id).orElse(null);

        if (existente != null) {
            existente.setNombre(empleado.getNombre());
            existente.setDocumento(empleado.getDocumento());
            existente.setCargo(empleado.getCargo());

            return repository.save(existente);
        }

        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        repository.deleteById(id);
    }

}