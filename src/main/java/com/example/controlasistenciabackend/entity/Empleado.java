package com.example.controlasistenciabackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ENTIDAD: Empleado
 * Mapea los datos de los colaboradores en la base de datos PostgreSQL.
 * Esta clase representa la tabla de persistencia para la gestión del personal.
 * * Estándar aplicado: PascalCase para el nombre de la clase.
 * * @author Jackson Montoya
 * @version 1.0
 */
@Entity
@Table(name = "empleados") // Buenas prácticas: Definir explícitamente el nombre de la tabla
public class Empleado {

    /**
     * Identificador único autoincremental de la tabla empleados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del empleado.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Documento de identidad (clave para la búsqueda y posterior generación del código QR).
     */
    @Column(nullable = false, unique = true)
    private String documento;

    /**
     * Cargo o puesto que ocupa el trabajador dentro de la organización.
     */
    @Column(nullable = false)
    private String cargo;

    /**
     * Constructor vacío requerido obligatoriamente por la especificación de JPA.
     */
    public Empleado() {
    }

    // ==========================================
    // MÉTODOS GETTERS Y SETTERS (Encapsulamiento)
    // Estándar aplicado: camelCase para los métodos
    // ==========================================

    public Long getId() {
        return id;
    }

    // Nota para el video: No se genera setter para el ID ya que es autoincremental

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}