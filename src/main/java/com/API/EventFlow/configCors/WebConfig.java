package com.API.EventFlow.configCors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para la aplicación.
 *
 * <p>
 * Esta clase implementa {@link WebMvcConfigurer} para registrar reglas CORS que se
 * aplican a las rutas REST de la API. La configuración actual permite orígenes
 * externos a acceder a los endpoints bajo {@code /api/**} y habilita los métodos
 * HTTP más comunes usados por un cliente web (GET, POST, PUT, DELETE, OPTIONS).
 * </p>
 *
 * <p>
 * Advertencia de seguridad: {@code allowedOrigins("*")} permite peticiones desde
 * cualquier origen y debe usarse con precaución en producción. Cuando sea posible,
 * reemplace {@code "*"} por la lista de orígenes de confianza (por ejemplo:
 * {@code https://mi-frontend.com}). También asegúrese de proteger los endpoints
 * sensibles (autorización) y no confiar únicamente en CORS como medida de seguridad.
 * </p>
 *
 * <p>
 * Uso típico: se carga automáticamente como un bean de configuración gracias a
 * {@link Configuration} y se aplica durante la inicialización del contexto Spring.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registra las reglas CORS para la API.
     *
     * <p>
     * Actualmente, se aplica a todas las rutas que comienzan con {@code /api/} y:
     * </p>
     * <ul>
     *   <li>Permite orígenes desde cualquier host ({@code allowedOrigins("*")})</li>
     *   <li>Permite los métodos HTTP: GET, POST, PUT, DELETE, OPTIONS</li>
     *   <li>Permite cualquier cabecera HTTP (incluyendo la cabecera {@code Authorization})</li>
     * </ul>
     *
     * <p>
     * Si necesita exponer cookies o credenciales en peticiones CORS, use
     * {@code allowCredentials(true)} y restrinja específicamente los orígenes.
     * </p>
     *
     * @param registry objeto usado para registrar las reglas CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica la configuración a todas las rutas bajo /api/
                .allowedOrigins("*") // Permite peticiones desde CUALQUIER origen (frontend)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*"); // Permite cualquier cabecera (incluyendo 'Authorization' para el token)
    }
}
