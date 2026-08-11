package com.example.controlasistenciabackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Filtro de rate limiting por dirección IP.
 * Implementa una ventana deslizante en memoria (sin dependencias externas):
 * registra los instantes de cada petición de una IP y rechaza con 429 si supera
 * el máximo permitido dentro de la ventana configurada.
 * <p>
 * Nota: al ser en memoria, el límite aplica por instancia. Para despliegues
 * multi-instancia se requeriría un store distribuido (p. ej. Redis).
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Map<String, Queue<Long>> registrosPorIp = new ConcurrentHashMap<>();

    @Value("${app.ratelimit.enabled:false}")
    private boolean habilitado;

    @Value("${app.ratelimit.max-requests:100}")
    private int maxRequests;

    @Value("${app.ratelimit.window-seconds:60}")
    private int windowSeconds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Solo aplica a la API y si está habilitado
        if (!habilitado || !request.getRequestURI().startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = obtenerIp(request);

        if (!permitir(ip)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":429,\"mensaje\":\"Demasiadas peticiones. Intenta nuevamente en unos segundos.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean permitir(String ip) {
        long ahora = System.currentTimeMillis();
        long inicioVentana = ahora - (windowSeconds * 1000L);
        Queue<Long> timestamps = registrosPorIp.computeIfAbsent(ip, k -> new ConcurrentLinkedQueue<>());

        synchronized (timestamps) {
            // Eliminar peticiones fuera de la ventana actual
            timestamps.removeIf(t -> t < inicioVentana);

            if (timestamps.size() >= maxRequests) {
                return false;
            }

            timestamps.add(ahora);
            return true;
        }
    }

    private String obtenerIp(HttpServletRequest request) {
        // Solo confiar en X-Forwarded-For si viene de un proxy confiable (nginx)
        // En producción, nginx siempre establece este header
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // Validar que la IP tenga formato IPv4/IPv6 básico
            String primeraIp = xff.split(",")[0].trim();
            if (esIpValida(primeraIp)) {
                return primeraIp;
            }
        }
        return request.getRemoteAddr();
    }

    private boolean esIpValida(String ip) {
        if (ip == null || ip.isBlank()) {
            return false;
        }
        // Validación básica: IPv4 (x.x.x.x) o IPv6 (contiene ':')
        return ip.matches("^[0-9a-fA-F:.]+$");
    }
}