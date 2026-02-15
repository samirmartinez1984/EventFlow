package com.API.EventFlow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un tipo de boleto disponible para un evento.
 *
 * <p>
 * La clase {@code TipoBoleto} encapsula la información de una categoría específica de boleto
 * para un evento determinado. Permite definir diferentes tipos de boletos con precios y
 * cantidades disponibles distintas (ej.: VIP, General).
 * </p>
 *
 * <p></p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 *   <li>Almacenar información de cada categoría de boleto (nombre, precio, disponibilidad)</li>
 *   <li>Mantener relación con él {@link Evento} al cual pertenece el boleto</li>
 *   <li>Registrar quién creó el tipo de boleto ({@link Usuario})</li>
 *   <li>Gestionar las {@link Compra}s asociadas a este tipo de boleto</li>
 * </ul>
 *
 * <p>
 * <strong>Relaciones JPA:</strong>
 * </p>
 * <ul>
 *   <li>Many-to-One con {@link Evento}: Múltiples tipos de boletos por evento</li>
 *   <li>One-to-Many con {@link Compra}: Un tipo de boleto puede tener múltiples compras</li>
 *   <li>Many-to-One con {@link Usuario}: Cada boleto es creado por un usuario administrador</li>
 * </ul>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Evento
 * @see Compra
 * @see Usuario
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoBoleto {

    /**
     * Identificador único del tipo de boleto.
     * <p>
     * Generado automáticamente por la base de datos mediante estrategia IDENTITY.
     * Actúa como clave primaria de la tabla de tipos de boletos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre o categoría del tipo de boleto.
     * <p>
     * Representa la denominación de la categoría de boleto (ej: "VIP", "General", "Estudiante").
     * Se almacena en la columna {@code nombre_tipo} de la base de datos.
     * </p>
     */
    @Column(name = "nombre_tipo")
    private String nombreTipo;

    /**
     * Precio unitario del boleto.
     * <p>
     * Representa el valor monetario de cada boleto de este tipo.
     * Se utiliza para calcular el total de la compra ({@code compraTotal}).
     * Utiliza {@link BigDecimal} para precisión en operaciones financieras.
     * </p>
     */
    private BigDecimal precio;

    /**
     * Cantidad de boletos disponibles para la venta.
     * <p>
     * Define cuántos boletos de este tipo están disponibles para comprar.
     * Disminuye con cada compra exitosa.
     * No puede ser nulo según las restricciones de base de datos.
     * Se almacena en la columna {@code boletos_disponibles} de la base de datos.
     * </p>
     */
    @Column(name = "boletos_disponibles", nullable = false)
    private Integer boletosDisponibles;

    /**
     * Evento al que pertenece este tipo de boleto.
     * <p>
     * Relación Many-to-One: Múltiples tipos de boletos pueden existir para un mismo evento.
     * Un tipo de boleto está asociado a exactamente un evento.
     * Se almacena mediante la columna {@code evento_id} en la base de datos.
     * </p>
     *
     * @see Evento
     */
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    /**
     * Lista de compras realizadas para este tipo de boleto.
     * <p>
     * Relación One-to-Many: Un tipo de boleto puede tener múltiples compras asociadas.
     * Implementa cascada de eliminación (CascadeType. ALL): cuando se elimina un tipo de boleto,
     * todas sus compras se eliminan automáticamente.
     * La opción orphanRemoval=true elimina las compras que pierdan referencia al tipo de boleto.
     * Se inicializa como una lista vacía para evitar problemas de null.
     * </p>
     *
     * @see Compra
     */
    @OneToMany(mappedBy = "tipoBoleto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Compra> compras = new ArrayList<>();

    /**
     * Usuario administrador que creó este tipo de boleto.
     * <p>
     * Relación Many-to-One: Múltiples tipos de boletos pueden ser creados por el mismo usuario.
     * Cada tipo de boleto tiene un único usuario responsable de su creación.
     * La carga es LAZY para optimizar el rendimiento (se carga bajo demanda).
     * Es un campo obligatorio (no puede ser nulo).
     * Se almacena mediante la columna {@code creado_por_id} en la base de datos.
     * </p>
     *
     * @see Usuario
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;
}
