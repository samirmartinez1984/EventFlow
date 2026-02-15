package com.API.EventFlow.config;


import com.API.EventFlow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de beans de aplicación relacionada con seguridad y autenticación.
 *
 * <p>
 * Esta clase registra beans clave utilizados por Spring Security:
 * </p>
 * <ul>
 *   <li>{@link UserDetailsService}: carga usuarios desde el repositorio por correo</li>
 *   <li>{@link AuthenticationProvider}: proveedor basado en DAO con bcrypt</li>
 *   <li>{@link PasswordEncoder}: implementa hashing de contraseñas con BCrypt</li>
 *   <li>{@link AuthenticationManager}: expone el manager para autenticación programática</li>
 * </ul>
 *
 * <p>
 * Notas importantes:
 * </p>
 * <ul>
 *   <li>Él {@code UserDetailsService} asume que el username del sistema es el correo.</li>
 *   <li>Si el repositorio cambia el nombre del método (ej: {@code findByUsername}),
 *       actualice este bean en consecuencia.</li>
 *   <li>BCrypt es recomendable en producción; el coste por defecto puede ajustarse si es necesario.</li>
 * </ul>
 *
 * @see UsuarioRepository
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UsuarioRepository usuarioRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        // ⚙️ CONFIGURAR: Si tu método se llama diferente (ej: findByUsername)
        return username -> usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
