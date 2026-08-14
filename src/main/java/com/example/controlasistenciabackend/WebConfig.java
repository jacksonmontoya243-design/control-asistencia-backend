package com.example.controlasistenciabackend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración Web MVC.
 * Registra un ResourceHandler para servir los códigos QR generados dinámicamente
 * ({@code /qr/**}) desde la ubicación del sistema de archivos donde
 * {@link com.example.controlasistenciabackend.service.QrGeneratorService} los persiste.
 * <p>
 * Spring Boot por defecto solo sirve recursos estáticos desde el classpath;
 * {@code QrGeneratorService} escribe en {@code file:static/qr/} (directorio de trabajo),
 * por lo que sin este mapeo las imágenes nunca se exponen vía HTTP.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea /qr/** → file:static/qr/  (relativo al working directory)
        // - Desarrollo local: working dir = raíz del proyecto → static/qr/
        // - Docker (producción): working dir = /app, volumen qr_data:/app/static/qr → file:static/qr/
        registry.addResourceHandler("/qr/**")
                .addResourceLocations("file:static/qr/");
    }
}