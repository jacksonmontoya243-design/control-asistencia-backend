package com.example.controlasistenciabackend.security;

import com.example.controlasistenciabackend.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la generación y validación de tokens JWT.
 * Firma con HS256 usando una clave secreta configurable por variable de entorno.
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Genera un token JWT para un usuario con su rol incluido.
     */
    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getRole().name());
        claims.put("username", usuario.getUsername());

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el username (subject) del token.
     */
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Extrae el rol del token.
     */
    public String extraerRole(String token) {
        return extraerClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Valida que el token sea válido y pertenezca al usuario indicado.
     */
    public boolean esTokenValido(String token, String username) {
        final String tokenUsername = extraerUsername(token);
        return (tokenUsername.equals(username) && !estaExpirado(token));
    }

    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}