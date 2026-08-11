package com.example.controlasistenciabackend.security;

import com.example.controlasistenciabackend.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro que intercepta cada petición HTTP, extrae el token JWT del header
 * Authorization (Bearer) y autentica al usuario en el contexto de Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtService.extraerUsername(token);
            final String role = jwtService.extraerRole(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Verificar que el usuario aún exista en BD
                if (usuarioRepository.findByUsername(username).isEmpty()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.esTokenValido(token, username)) {
                    // Autoridad con prefijo ROLE_ para que @PreAuthorize("hasRole('ADMIN')") funcione
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido o expirado: no autenticar, dejar pasar (Spring Security devolverá 401/403)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}