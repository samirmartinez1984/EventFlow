package com.API.EventFlow.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) para operaciones de Evento.
 *
 * <p>
 * La clase {@code EventoDTO} se utiliza para transferir datos entre las capas
 * de presentación (API REST) y la capa de lógica de negocio. Es agnóstica respecto
 * a la entidad JPA {@link com.API.EventFlow.model.Evento}, permitiendo una
 * separación clara entre la API y el modelo de persistencia.
 * </p>
 *
 * <p>
 * <strong>Propósito:</strong>
 * </p>
 * <ul>
 *   <li>Validar datos de eventos recibidos desde peticiones HTTP</li>
 *   <li>Transferir datos entre controlador y servicios de negocio</li>
 *   <li>Encapsular la estructura de solicitud para crear/actualizar eventos</li>
 *   <li>Proteger la entidad JPA {@code Evento} de cambios no autorizados</li>
 * </ul>
 *
 * <p>
 * <strong>Casos de uso:</strong>
 * </p>
 * <ul>
 *   <li>Crear nuevo evento: POST /api/eventos</li>
 *   <li>Actualizar evento: PUT /api/eventos/{id}</li>
 * </ul>
 *
 * <p>
 * <strong>Validaciones:</strong>
 * Se aplican automáticamente mediante anotaciones Jakarta Validation:
 * </p>
 * <ul>
 *   <li>El nombre del evento no puede estar vacío</li>
 *   <li>La fecha debe ser posterior a la fecha actual (validación en servicio)</li>
 *   <li>La capacidad máxima debe estar entre 1 y 10,000 personas</li>
 * </ul>
 *
 * <p>
 * <strong>Nota:</strong> El usuario creador del evento se obtiene del contexto
 * de seguridad (token JWT) y se asigna automáticamente en el servicio.
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see com.API.EventFlow.model.Evento
 * @see com.API.EventFlow.service.EventoService
 * @see com.API.EventFlow.controller.EventoController
 */
@Getter
@Setter
public class EventoDTO {

    /**
     * Identificador único del evento.
     * <p>
     * Campo solo de lectura que se incluye en respuestas después de crear/actualizar.
     * No se proporciona en peticiones de creación (se genera automáticamente).
     * </p>
     */
    private Long id;

    /**
     * Nombre o título descriptivo del evento.
     * <p>
     * Representa el nombre que será mostrado a los usuarios. Debe ser único
     * y descriptivo para facilitar la identificación del evento.
     * Ejemplos: "Conferencia Tech 2026", "Festival de Música", "Concierto Bach"
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotBlank: No puede estar vacío o contener solo espacios en blanco</li>
     *   </ul>
     */
    @NotBlank(message = "El nombre del evento no puede estar vacio")
    private String nombreEvento;

    /**
     * Fecha y hora de realización del evento.
     * <p>
     * Especifica cuándo se llevará a cabo el evento. Debe ser una fecha
     * futura (posterior a la fecha actual). La validación temporal se realiza
     * en el servicio {@code EventoService} mediante anotación {@code @Future}.
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>Validación adicional: @Future en la entidad (no en DTO)</li>
     *   </ul>
     *
     * @example 2026-12-31T20:30:00
     */
    private LocalDateTime fechaEvento;

    /**
     * Capacidad máxima de asistentes del evento.
     * <p>
     * Define el número máximo de personas que pueden asistir al evento.
     * Determina la cantidad total de boletos disponibles.
     * Rango válido: 1 a 10,000 personas.
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotNull: Valor obligatorio</li>
     *     <li>@Min: Mínimo 1 asistente</li>
     *     <li>@Max: Máximo 10,000 asistentes</li>
     *   </ul>
     *
     * @example 50, 500, 5000
     */
    @NotNull
    @Min(1)
    @Max(10000)
    private Integer capacidadMaxima;

}
