package com.example.controlasistenciabackend.service;

import com.example.controlasistenciabackend.entity.Empleado;
import com.example.controlasistenciabackend.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    public Empleado guardarEmpleado(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public Empleado actualizarEmpleado(Long id, Empleado empleadoActualizado) {

        Empleado empleado = empleadoRepository.findById(id).orElse(null);

        if (empleado != null) {
            empleado.setNombre(empleadoActualizado.getNombre());
            empleado.setDocumento(empleadoActualizado.getDocumento());
            empleado.setCargo(empleadoActualizado.getCargo());

            return empleadoRepository.save(empleado);
        }

        return null;
    }
    public void eliminarEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }}