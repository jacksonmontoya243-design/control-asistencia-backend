package com.example.controlasistenciabackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Orígenes permitidos:
        // - Cualquier subdominio de Netlify (donde se desplegará el frontend)
        // - localhost en cualquier puerto (desarrollo local con Vite)
        // - El backend desplegado en Render (por compatibilidad)
        config.setAllowedOriginPatterns(List.of(
                "https://*.netlify.app",
                "http://localhost:*",
                "https://control-asistencia-backend-1.onrender.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // La aplicación usa JWT en el header Authorization (no cookies),
        // por lo que no se requieren credenciales.
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
