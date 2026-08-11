# Guía de Despliegue con Docker - Control de Asistencia

Este proyecto ha sido dockerizado para asegurar que sea **descargable, escalable y portable**. Esto significa que funcionará en cualquier ordenador que tenga Docker instalado, sin necesidad de configurar Java, Node.js o PostgreSQL manualmente.

## Requisitos Previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución.

## Instrucciones para Iniciar el Proyecto

1. **Descargar/Clonar** el proyecto en tu ordenador.
2. Abre una terminal (PowerShell, CMD o Bash) en la carpeta raíz del proyecto.
3. Ejecuta el siguiente comando para construir e iniciar todos los servicios:

   ```bash
   docker-compose up --build -d
   ```

4. **¡Listo!** El sistema estará disponible en las siguientes direcciones:
   - **Frontend (Interfaz de Usuario):** [http://localhost](http://localhost)
   - **Backend (API):** [http://localhost:8080](http://localhost:8080)
   - **Base de Datos (PostgreSQL):** Puerto 5432

## Credenciales por Defecto

- **Usuario:** `admin`
- **Contraseña:** `admin` (o cualquier texto, ya que la validación está simplificada para pruebas).

## Ventajas de esta Configuración

- **Escalabilidad:** Puedes levantar múltiples instancias del backend si el tráfico aumenta (usando un balanceador de carga).
- **Persistencia:** Los datos de la base de datos y los códigos QR generados se guardan en **volúmenes de Docker**, por lo que no se pierden al apagar los contenedores.
- **Aislamiento:** No hay conflictos con otras versiones de Java o PostgreSQL que tengas instaladas en tu equipo.

## Notas para Producción

Si planeas usar esto en una red real o en la nube:
1. Cambia las contraseñas en el archivo `docker-compose.yml`.
2. Actualiza la variable `APP_QR_BASE_URL` en el `docker-compose.yml` con la IP real de tu servidor para que los códigos QR sean escaneables desde móviles en la misma red.
