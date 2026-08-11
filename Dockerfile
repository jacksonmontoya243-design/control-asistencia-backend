# Stage 1: Build with Maven
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with JRE (imagen más ligera)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/control-asistencia-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Se fuerza el perfil prod para no arrancar nunca con config de desarrollo
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]