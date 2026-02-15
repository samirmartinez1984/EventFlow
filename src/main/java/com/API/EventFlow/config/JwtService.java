package com.API.EventFlow.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Servicio utilitario para generar y validar tokens JWT.
 *
 * <p>
 * Proporciona métodos para generar tokens firmados, extraer el sujeto (correo),
 * verificar validez y comprobar expiración. Usa una clave secreta en base64
 * configurada en las propiedades de la aplicación bajo {@code jwt.secret}.
 * </p>
 *
 * <p>
 * Notas de seguridad:
 * </p>
 * <ul>
 *   <li>No exponga la clave secreta en repositorios públicos.</li>
 *   <li>Configure una duración de token adecuada y considere usar refresh tokens
 *       para sesiones prolongadas.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String claveSecreta;

    // Generar token
    public String generarToken(UserDetails usuario) {
        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer username (correo) del token
    public String extractCorreo(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Validar token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractCorreo(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Verificar si el token está expirado
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Extraer todos los claims del token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Obtener la clave de firma
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(claveSecreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}