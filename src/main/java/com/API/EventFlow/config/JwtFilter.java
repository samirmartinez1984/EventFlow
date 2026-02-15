package com.API.EventFlow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que intercepta las solicitudes HTTP y válida el token JWT presente
 * en la cabecera {@code Authorization} (Bearer token).
 *
 * <p>
 * Si el token es válido, establece la autenticación en el contexto de seguridad
 * para que Spring Security reconozca al usuario en downstream handlers y controladores.
 * </p>
 *
 * <p>
 * El filtro salta (no válida) para rutas públicas como {@code /api/autenticacion} y
 * otras rutas públicas definidas en la aplicación.
 * </p>
 *
 * <p>
 * Nota: este filtro se registra en la cadena de seguridad mediante
 * {@code SecurityConfig} para ejecutarse antes del procesamiento estándar de
 * username/password.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Intercepta cada petición y válida el token JWT si está presente.
     *
     * <p>
     * Flujo resumido:
     * </p>
     * <ol>
     *   <li>Si la ruta es pública, pasa la petición sin validar.</li>
     *   <li>Extrae la cabecera Authorization y obtiene el token ("Bearer ...").</li>
     *   <li>Extrae el correo del token y carga el {@code UserDetails} asociado.</li>
     *   <li>Válida el token y, en caso afirmativo, crea un {@code UsernamePasswordAuthenticationToken}
     *       y lo establece en él {@code SecurityContextHolder}.</li>
     *   <li>Continúa la cadena de filtros.</li>
     * </ol>
     *
     * @param request petición HTTP entrante
     * @param response respuesta HTTP
     * @param filterChain cadena de filtros
     * @throws ServletException en errores de servlet
     * @throws IOException en errores de IO
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Rutas públicas que no requieren autenticación
        if (path.startsWith("/api/autenticacion") || path.startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extracción del token JWT
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String correoExtraido = jwtService.extractCorreo(token); // debe extraer el subject


        // Verificación de autenticación previa
        if (correoExtraido != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(correoExtraido);

            if (jwtService.isTokenValid(token, userDetails)) {
                // ✅ Seteamos el correo como principal en el contexto de seguridad
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                correoExtraido, // principal = correo
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
