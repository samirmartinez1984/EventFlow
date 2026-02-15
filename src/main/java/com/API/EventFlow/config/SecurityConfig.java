package com.API.EventFlow.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de la aplicación (Spring Security).
 *
 * <p>
 * Esta clase define la cadena de filtros de seguridad, las reglas de autorización
 * por ruta y registra el filtro JWT personalizado ( {@link JwtFilter} ) para
 * validar tokens en cada petición.
 * </p>
 *
 * <p>
 * Reglas principales definidas:
 * </p>
 * <ul>
 *   <li>Se permiten peticiones preflight CORS (OPTIONS) a todas las rutas.</li>
 *   <li>Rutas públicas: {@code /api/autenticacion/**}, Swagger y OpenAPI</li>
 *   <li>Rutas protegidas: lectura/creación de recursos para roles {@code CLIENTE} y {@code ADMIN}</li>
 *   <li>Operaciones de gestión de eventos y tipos de boleto restringidas a {@code ADMIN}</li>
 *   <li>Por defecto, cualquier otra petición requiere autenticación.</li>
 * </ul>
 *
 * <p>
 * Además, la aplicación utiliza sesiones sin estado (JWT): se establece
 * {@code SessionCreationPolicy.STATELESS} y se delega la autenticación a un
 * {@link AuthenticationProvider} y al filtro JWT.
 * </p>
 *
 * <p>
 * Nota de seguridad: revise cuidadosamente las rutas expuestas en {@code permitAll()}
 * y asegúrese de que los endpoints críticos estén protegidos por roles y validaciones adicionales.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // =================================================================
                        // LA REGLA QUE FALTABA: Permitir todas las peticiones preflight de CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // =================================================================

                        // 🌐 Rutas Públicas: Todos pueden acceder
                        .requestMatchers("/api/autenticacion/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 👥 Rutas para Clientes y Admins (Leer datos)
                        .requestMatchers(HttpMethod.GET, "/api/eventos/**", "/api/tipoboletos/**", "/api/compras/**").hasAnyAuthority("CLIENTE", "ADMIN")

                        // 👥 Rutas para Clientes y Admins (Crear datos)
                        .requestMatchers(HttpMethod.POST, "/api/compras/**").hasAnyAuthority("CLIENTE", "ADMIN")

                        // 👑 Rutas solo para Admin (Control total sobre eventos y boletos)
                        .requestMatchers("/api/eventos/**", "/api/tipoboletos/**").hasAuthority("ADMIN")

                        // 🔒 Por defecto, cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
