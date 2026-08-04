# 🚀 Sistema Inteligente de Control de Asistencia

## 📋 Descripción

Aplicación web para el registro y seguimiento de entradas y salidas del personal. Permite gestionar empleados mediante operaciones **CRUD** y automatizar el control de asistencia con **códigos QR individuales**, usando una arquitectura **Full-Stack** moderna.

## 🛠️ Tecnologías

| Capa | Tecnologías |
| ---- | ----------- |
| **Backend** | Java 17, Spring Boot, Spring Data JPA, Spring Security, Maven, MySQL |
| **Frontend** | React (Vite), Axios, React Router DOM, CSS3 |
| **Complementos** | Git, GitHub, ZXing (QR) |

## 💡 Funcionalidades

- **Gestión de Empleados:** CRUD completo con tablas dinámicas.
- **Asistencia:** Generación automática de códigos QR por empleado.
- **Dashboard:** Estadísticas de empleados, supervisores y administradores en tiempo real.
- **Seguridad:** Autenticación con Spring Security.

## 📂 Estructura

```plaintext
control-asistencia-backend/
│   └── API REST - Spring Boot
frontend-asistencia/
    └── Aplicación Web - React + Vite
```

## 🏗️ Arquitectura

```text
Frontend React → API REST (Spring Boot) → Service Layer → Repository Layer → MySQL
```

## 📋 Auditoría del Proyecto

> **Fecha:** 8 de abril de 2026

### Resumen Ejecutivo

| Aspecto | Estado | Observación |
| ------- | ------ | ----------- |
| Arquitectura | ✅ | Separación clara por capas |
| Compilación | ✅ | Maven, Java 17, Spring Boot 3.5.14 |
| Frontend | ✅ | React 19, Vite 8, proxy al backend |
| Seguridad | ⚠️ | CSRF deshabilitado, endpoints abiertos |
| Autenticación | ⚠️ | Login funcional, sin JWT/sesiones |
| Base de Datos | ✅ | MySQL 8 con variables de entorno |
| Pruebas | ⚠️ | Solo test de contexto |
| Despliegue | ✅ | Docker Compose (db, backend, frontend) |

### Endpoints

| Método | Endpoint | Descripción |
| ------ | -------- | ----------- |
| POST | `/api/auth/register` | Registro de usuarios |
| POST | `/api/auth/login` | Inicio de sesión |
| GET | `/api/empleados` | Lista empleados |
| POST | `/api/empleados` | Crea empleado y genera QR |
| PUT | `/api/empleados/{id}` | Actualiza empleado |
| DELETE | `/api/empleados/{id}` | Elimina empleado |
| GET | `/api/empleados/count` | Total de empleados |
| GET | `/api/empleados/count-stats` | Estadísticas por cargo |

### Seguridad

- **Configuración:** CSRF deshabilitado, `permitAll()` en todos los endpoints.
- **Hallazgos:**
  - 🔴 Endpoints abiertos sin autenticación.
  - 🟡 Contraseña admin por defecto (`admin/admin`).
  - 🟡 Sin JWT ni sesiones HTTP.
  - 🟢 Contraseñas con BCrypt.
  - 🟢 Credenciales DB por variables de entorno.

### Modelo de Datos

**Tabla `empleados`:** `id` (PK), `nombre`, `documento`, `cargo`.

**Tabla `usuarios`:** `id` (PK), `username` (único), `password` (hash BCrypt).

### Códigos QR

- **Generación:** ZXing, 300x300 px, PNG.
- **Contenido:** URL `{base-url}/empleado/{id}`.
- **Almacenamiento:** `/static/qr/empleado_{id}.png`.
- **Persistencia:** Volumen Docker `qr_data`.

### Docker

| Servicio | Imagen | Puerto |
| -------- | ------ | ------ |
| `db` | `mysql:8.0` | 3306 |
| `backend` | Dockerfile propio | 8080 |
| `frontend` | Dockerfile propio | 80 |

**Variables de entorno:** `MYSQL_DATABASE`, `MYSQL_ROOT_PASSWORD`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `APP_QR_BASE_URL`.

### Pruebas

Solo existe un test de contexto. Faltan pruebas unitarias para servicios, controladores y repositorios.

### Deuda Técnica

- `EmpleadoController` usa `@Autowired` en campos (preferir constructor).
- `actualizar()` devuelve `null` en lugar de `404`.
- Sin validación de datos (`@Valid`) ni manejo global de excepciones.
- `qrgeneratorService` escribe en el sistema de archivos sin ruta inyectable.
- `DataInitializer` crea admin con contraseña fija.
- Sin perfiles Spring (dev/prod) ni logs SLF4J.
- Archivos estáticos HTML antiguos duplican la SPA React.
- Assets y CSS de plantilla Vite sin uso.

### Resumen de Hallazgos

**🔴 Críticos:**
1. API abierta (`permitAll`).
2. Sin JWT ni sesiones.

**🟡 Importantes:**
3. Credenciales por defecto.
4. Cobertura de pruebas insuficiente.
5. Doble frontend (HTML estático + React).
6. `EmpleadoService` infrautilizado.

**🟢 Correctos:**
7. BCrypt en contraseñas.
8. Configuración por variables de entorno.
9. Docker multi-etapa eficiente.
10. Persistencia en volúmenes.
11. Proxy Nginx correcto.

### Recomendaciones

| Prioridad | Acción |
| --------- | ------ |
| 1 | Implementar JWT o sesiones para proteger endpoints |
| 2 | Cambiar contraseñas por defecto a variables de entorno |
| 3 | Añadir pruebas unitarias e integración |
| 4 | Usar `EmpleadoService` en el controller |
| 5 | Eliminar frontend HTML estático antiguo |
| 6 | Validación de datos y manejo global de excepciones |
| 7 | Reemplazar `System.out.println` por SLF4J |
| 8 | Eliminar assets de plantilla Vite sin uso |

## 👨‍💻 Autor

**Jackson Montoya Mercado** — 📅 2026
