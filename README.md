# 🚀 Sistema Inteligente de Control de Asistencia

Aplicación web full-stack para el registro y seguimiento de entradas y salidas del personal mediante **códigos QR individuales**.

## 🛠️ Tecnologías

| Capa | Tecnologías |
| ---- | ----------- |
| **Backend** | Java 17, Spring Boot 3.5.14, Spring Security (JWT), Spring Data JPA, Maven, PostgreSQL 16 |
| **Frontend** | React 19, Vite 8, Axios, React Router DOM 7, HTML5-QRCode |
| **Infraestructura** | Docker, Docker Compose, Nginx (HTTPS), GitHub Actions (CI/CD) |
| **Complementos** | ZXing (generación QR), JJWT 0.12.6, Lombok, Logback |

## 💡 Funcionalidades

- **Autenticación JWT** con roles: `ADMIN`, `SUPERVISOR`, `EMPLEADO`.
- **Gestión de Empleados:** CRUD completo, búsqueda y paginación.
- **Asistencias:** Registro entrada/salida por escaneo QR (cámara), reportes por empleado y/o rango de fechas, resumen de entradas/salidas.
- **Dashboard:** Estadísticas de empleados, administradores y supervisores; últimas asistencias.
- **Gestión de Usuarios (solo ADMIN):** listar, cambiar roles y eliminar (con protección anti-auto-eliminación).
- **Seguridad:** JWT stateless, BCrypt, rate limiting por IP, cabeceras HSTS/deny frames, HTTPS forzado en producción.
- **QR:** Generación automática de códigos por empleado (300x300 PNG) servidos por `/qr/{empleado}.png`.

## 📂 Estructura

```plaintext
control-asistencia-backend/
├── .github/workflows/      # CI (build+test) y CD (publicar imágenes GHCR)
├── frontend-asistencia/    # Aplicación React + Vite
├── src/
│   ├── main/java/com/example/controlasistenciabackend/
│   │   ├── controller/     # Auth, Empleado, Asistencia, User
│   │   ├── dto/            # Requests/responses con validación
│   │   ├── entity/         # Empleado, Usuario, Asistencia (roles y tipos)
│   │   ├── exception/      # Manejador global de errores
│   │   ├── repository/     # Capa JPA
│   │   ├── security/       # JWT service/filter, rate limiting, UserDetails
│   │   └── service/        # Lógica de negocio (Auth, Empleado, Asistencia, User, QR)
│   ├── main/resources/     # application.properties, perfiles dev/prod, logback
│   └── test/               # Tests unitarios (servicios + controllador)
├── Dockerfile              # Backend multi-etapa
├── docker-compose.yml      # PostgreSQL + Backend + Frontend (con HTTPS)
└── nginx.conf              # Proxy inverso, HTTPS y cabeceras de seguridad
```

## 🏗️ Arquitectura

```text
React SPA (Nginx/443) → API REST (Spring Boot) → Service → Repository → PostgreSQL
        ↑                        ↑
     /api, /qr            JWT + Rate Limit + Roles
```

## 🔐 Seguridad

| Medida | Estado |
| ------ | ------ |
| **JWT** (HS256, expiración configurable) | ✅ |
| **Contraseñas BCrypt** | ✅ |
| **Roles y autorización** (`@PreAuthorize`) | ✅ |
| **Rate limiting por IP** (ventana deslizante, 429) | ✅ (prod) |
| **Sesiones stateless** (`SessionCreationPolicy.STATELESS`) | ✅ |
| **HSTS / X-Frame-Options DENY / nosniff** | ✅ |
| **HTTPS forzado** (redirect + proxy `X-Forwarded-Proto`) | ✅ (prod) |
| **Protección escalada de privilegios** (registro siempre asigna EMPLEADO) | ✅ |
| **Protección auto-eliminación de usuario** | ✅ |
| **CSRF** | Deshabilitado (API stateless con JWT) |

**Endpoints protegidos por rol:**
- Públicos: `POST /api/auth/login`, `GET /qr/**`
- `ADMIN`: `POST /api/auth/register`, `POST/PUT/DELETE /api/empleados/**`, `/api/usuarios/**`
- `ADMIN, SUPERVISOR`: `GET /api/empleados`, `GET /api/empleados/all`, `/count`, `/count-stats`
- Autenticados: asistencias (`/api/asistencias/**`), dashboard

## 🔌 Endpoints de la API

| Método | Endpoint | Descripción |
| ------ | -------- | ----------- |
| POST | `/api/auth/login` | Inicio de sesión → `{ token, username, role, expiresIn }` |
| POST | `/api/auth/register` | Registra usuario (rol siempre EMPLEADO) |
| GET | `/api/empleados?termino=&page=&size=` | Lista paginada con búsqueda |
| GET | `/api/empleados/all` | Lista completa (sin paginar) |
| POST | `/api/empleados` | Crea empleado y genera QR |
| PUT | `/api/empleados/{id}` | Actualiza empleado |
| DELETE | `/api/empleados/{id}` | Elimina empleado |
| GET | `/api/empleados/count` | Total de empleados |
| GET | `/api/empleados/count-stats` | Estadísticas (empleados, admins, supervisores) |
| POST | `/api/asistencias` | Registra asistencia (`empleadoId`, `tipo: ENTRADA/SALIDA`) |
| GET | `/api/asistencias` | Lista todas (descendente) |
| GET | `/api/asistencias/empleado/{id}` | Asistencias de un empleado |
| GET | `/api/asistencias/reporte` | Reporte por empleado y/o rango de fechas |
| GET | `/api/asistencias/resumen` | Resumen entradas/salidas en rango |
| GET | `/api/usuarios` | Lista usuarios (solo ADMIN) |
| PUT | `/api/usuarios/{id}/role` | Cambia rol (solo ADMIN) |
| DELETE | `/api/usuarios/{id}` | Elimina usuario (solo ADMIN) |

## 🗄️ Modelo de Datos

**`empleados`**: `id` (PK), `nombre`, `documento` (único), `cargo`.

**`usuarios`**: `id` (PK), `username` (único), `password` (BCrypt), `role` (`ADMIN`/`SUPERVISOR`/`EMPLEADO`).

**`asistencias`**: `id` (PK), `empleadoId`, `fechaHora`, `tipo` (`ENTRADA`/`SALIDA`).

## 🧪 Pruebas

| Suite | Alcance |
| ----- | ------- |
| `AuthServiceTest` | Registro (users duplicados, escalada), login (credenciales, JWT) |
| `AsistenciaServiceTest` | Registro, empleado inexistente, persistencia |
| `UserServiceTest` | Obtener, cambiar rol, eliminar (propio/otro) |
| `EmpleadoControllerTest` | Validación, creación + QR, 404s, autorización, paginación |

Ejecutar:

```bash
mvn -B test
```

## 🐳 Docker

### Servicios

| Servicio | Imagen | Puerto |
| -------- | ------ | ------ |
| `db` | `postgres:16-alpine` | 5432 |
| `backend` | Dockerfile propio (multi-etapa JRE Alpine) | 8080 |
| `frontend` | Node 20 build → Nginx Alpine (HTTPS 443) | 80→443 |

### Variables de entorno (`.env`)

```env
POSTGRES_DB=control_asistencia
POSTGRES_USER=postgres
POSTGRES_PASSWORD=tu_contraseña_segura_aqui
SPRING_PROFILE=prod
JWT_SECRET=clave-secreta-super-segura-cambiar-en-produccion
JWT_EXPIRATION=86400000
APP_QR_BASE_URL=https://tu-dominio.com
ADMIN_USERNAME=admin
ADMIN_PASSWORD=tu_password_admin_segura
```

### Levantar

```bash
docker compose up -d --build
```

**Notas:**
- El frontend redirige HTTP → HTTPS y sirve con certificado autofirmado por defecto; reemplazar montando volúmenes sobre `/etc/nginx/ssl`.
- El QR se persiste en el volumen `qr_data`.
- En producción `SPRING_PROFILE=prod` activa rate limiting y HTTPS forzado.

## 🔄 CI/CD (GitHub Actions)

- **CI:** compila backend (`mvn test`) y construye frontend (`npm run build`) en cada push/PR a `main`.
- **CD:** construye y publica imágenes `ghcr.io/{owner}/control-asistencia-{backend,frontend}:latest` (si `GHCR_USERNAME` y `GHCR_PAT` están configurados).

## 📁 Configuración de Perfiles

| Perfil | Uso |
| ------ | --- |
| `dev` | PostgreSQL local, SQL visible, rate limit desactivado, sin HTTPS |
| `prod` | DB en Docker (`db:5432`), logs INFO + archivo rotativo, rate limit 100 req/min, HTTPS forzado |

Selección por variable: `SPRING_PROFILE=prod` (o `--spring.profiles.active=prod`).

## 🧹 Auditoría y Mejoras Recientes

**Hallazgos corregidos en esta auditoría (11/08/2026):**
- 🔴 Eliminado archivo duplicado `src/main/java/com/example/control` (copia sin extensión de `UserDetailsServiceImpl`).
- 🔴 Eliminados archivos de config del IDE con rutas locales del sistema (`control-asistencia-backend.eml`, `control-asistencia-backend.userlibraries`).
- 🔴 Eliminados `package.json`/`package-lock.json` de la raíz (obsoletos; el frontend es independiente).
- 🟡 Eliminados HTML/CSS/JS estáticos antiguos que duplicaban la SPA React.
- 🟡 Eliminados archivos de evidencia (`GA5-220501095-AA1-EV08*`) del repositorio.
- 🟡 Eliminados códigos QR generados dinámicamente (ya no deben committearse).
- 🟢 Corregidos imports de `App.jsx` para usar `./components/` (minúsculas) y evitar fallos de build en Linux/CI.
- 🟢 Actualizado `.gitignore` para excluir `.eml`, `.userlibraries`, `.zip`, `GA5*/` y `static/qr/`.

**Deuda técnica pendiente (recomendaciones para próximas iteraciones):**
- Migrar `EmpleadoController` de `@Autowired` en campos a inyección por constructor.
- Mover la ruta configurable del QR a variable de entorno (`app.qr.path`).
- Añadir tests de integración con H2/Testcontainers y tests para `AuthController`, `AsistenciaController` y `UserController`.
- Separar el `JwtAuthenticationFilter` para manejar tokens expirados o malformados con respuestas JSON claras.
- Considerar Redis como store distribuido para el rate limiting multi-instancia.

## 👨‍💻 Autor

**Jackson Montoya Mercado** — 📅 2026
