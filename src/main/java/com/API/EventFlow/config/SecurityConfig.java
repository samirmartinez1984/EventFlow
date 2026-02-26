package com.API.EventFlow.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de la aplicación (Spring Security).
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 🌐 Rutas Públicas: No requieren autenticación
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🛒 Clientes y Admins: Compras
                        .requestMatchers(HttpMethod.GET, "/api/compras/**").hasAnyAuthority("CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/compras/**").hasAnyAuthority("CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/compras/**").hasAnyAuthority("CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/compras/**").hasAnyAuthority("CLIENTE", "ADMIN")

                        // Solo Admin: Mutaciones de eventos y boletos
                        .requestMatchers(HttpMethod.POST, "/api/eventos/**", "/api/tipoboletos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/eventos/**", "/api/tipoboletos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/**", "/api/tipoboletos/**").hasAuthority("ADMIN")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
