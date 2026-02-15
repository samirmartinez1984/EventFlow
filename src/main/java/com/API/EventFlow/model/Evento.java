package com.API.EventFlow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un evento en el sistema EventFlow.
 *
 * <p>
 * La clase {@code Evento} es una entidad JPA que encapsula toda la información relacionada
 * con un evento, incluyendo su nombre, fecha de realización, capacidad máxima de asistentes,
 * el usuario creador del evento, y los diferentes tipos de boletos disponibles.
 * </p>
 *
 * <p>
 * <strong>Características principales:</strong>
 * </p>
 * <ul>
 *   <li>Almacenamiento persistente en base de datos mediante JPA</li>
 *   <li>Validaciones automáticas de datos (fecha futura, no nulidad)</li>
 *   <li>Relación con la entidad {@link Usuario} (muchos eventos por usuario)</li>
 *   <li>Relación con la entidad {@link TipoBoleto} (un evento puede tener múltiples tipos de boletos)</li>
 *   <li>Cascada de eliminación de boletos cuando se elimina el evento</li>
 * </ul>
 *
 * <p>
 * <strong>Anotaciones Lombok utilizadas:</strong>
 * </p>
 * <ul>
 *   <li>{@code @Getter}: Genera automáticamente métodos getters para todos los campos</li>
 *   <li>{@code @Setter}: Genera automáticamente métodos setters para todos los campos</li>
 *   <li>{@code @NoArgsConstructor}: Genera un constructor sin argumentos</li>
 *   <li>{@code @AllArgsConstructor}: Genera un constructor con todos los campos como parámetros</li>
 * </ul>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Usuario
 * @see TipoBoleto
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    /**
     * Identificador único del evento.
     * <p>
     * Generado automáticamente por la base de datos mediante estrategia IDENTITY.
     * Actúa como clave primaria de la tabla de eventos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre o título del evento.
     * <p>
     * Representa el nombre descriptivo del evento que se mostrará a los usuarios.
     * Se almacena en la columna {@code nombre_evento} de la base de datos.
     * </p>
     */
    @Column(name = "nombre_evento")
    private String nombreEvento;

    /**
     * Fecha y hora de realización del evento.
     * <p>
     * Debe ser una fecha futura (después de la fecha actual) según la validación
     * {@link Future}. No puede ser nulo, según la validación {@link NotNull}.
     * Se almacena en la columna {@code fecha_evento} de la base de datos.
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@Future: Garantiza que la fecha sea posterior a la fecha actual</li>
     *     <li>@NotNull: Garantiza que el valor no sea nulo</li>
     *   </ul>
     */
    @Future(message = "La fecha del evento debe ser futura.") // Asegura que la fecha no haya pasado.
    @NotNull(message = "La fecha no puede ser nula.")
    @Column(name = "fecha_evento")
    private LocalDateTime fechaEvento;

    /**
     * Capacidad máxima de asistentes del evento.
     * <p>
     * Define el número máximo de personas que pueden asistir al evento.
     * Se almacena en la columna {@code capacidad_maxima} de la base de datos.
     * Este valor se utiliza para controlar la disponibilidad de boletos.
     * </p>
     */
    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    /**
     * Usuario propietario/creador del evento.
     * <p>
     * Relación Many-to-One: Un {@link Usuario} puede crear múltiples eventos,
     * pero cada evento está asociado a un único usuario.
     * La carga es LAZY para optimizar el rendimiento (se carga bajo demanda).
     * Es un campo obligatorio (no puede ser nulo).
     * </p>
     *
     * @see Usuario
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Lista de tipos de boletos disponibles para este evento.
     * <p>
     * Relación One-to-Many: Un evento puede tener múltiples tipos de boletos.
     * Se inicializa como una lista vacía para evitar problemas de null.
     * Implementa cascada de eliminación (CascadeType. ALL): cuando se elimina un evento,
     * todos sus tipos de boletos se eliminan automáticamente.
     * La opción orphanRemoval=true elimina los boletos que no tengan referencia al evento.
     * </p>
     *
     * @see TipoBoleto
     */
    @OneToMany(mappedBy = "evento",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoBoleto> tipoBoletos = new ArrayList<>();
}
