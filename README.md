# Sistema de Control de Asistencia de Empleados

## Descripción del Proyecto

El Sistema de Control de Asistencia de Empleados es una aplicación web desarrollada para optimizar el registro y seguimiento de entradas y salidas del personal dentro de una organización.

La solución permite gestionar la información de los empleados mediante operaciones CRUD (Crear, Consultar, Actualizar y Eliminar) y generar códigos QR individuales para cada empleado, facilitando la automatización de procesos de control de asistencia.

---

## Objetivo

Desarrollar un sistema que permita administrar la información de los empleados y apoyar el proceso de control de asistencia mediante tecnologías modernas de desarrollo de software y conexión a bases de datos.

---

## Tecnologías Utilizadas

### Backend

* Java 17
* Spring Boot
* Spring Data JPA
* JDBC
* Maven
* Spring Security

### Base de Datos

* MySQL

### Frontend

* HTML5
* CSS3
* JavaScript

### Control de Versiones

* Git
* GitHub

### Generación de QR

* ZXing (Zebra Crossing)

---

## Funcionalidades Implementadas

### Gestión de Empleados

* Registrar empleados.
* Consultar empleados.
* Actualizar información de empleados.
* Eliminar empleados.

### Gestión de Asistencia

* Generación automática de códigos QR.
* Visualización de códigos QR por empleado.
* Administración centralizada de registros.

### Seguridad

* Configuración básica de autenticación mediante Spring Security.

---

## Estructura General del Proyecto

```text
src/
│
├── controller/
├── entity/
├── repository/
├── service/
├── security/
├── config/
│
└── resources/
    ├── static/
    └── application.properties
```

## Arquitectura Implementada

El proyecto sigue una arquitectura por capas:

```text
Cliente
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
Base de Datos MySQL
```

---

## Base de Datos

Motor utilizado:

* MySQL

Conexión realizada mediante JDBC y administrada por Spring Data JPA.

---

## Evidencia Académica

Proyecto desarrollado como evidencia:

**GA7-220501096-AA2-EV01 – Codificación de módulos del software según requerimientos del proyecto**

Programa de formación:

**Tecnólogo en Análisis y Desarrollo de Software (SENA)**

---

## Autor

**Jackson Montoya**

Tecnólogo en Análisis y Desarrollo de Software

Servicio Nacional de Aprendizaje – SENA

2026
