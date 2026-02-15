package com.API.EventFlow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una transacción de compra de boletos.
 *
 * <p>
 * La clase {@code Compra} encapsula toda la información relacionada con una transacción
 * de compra de boletos realizada por un usuario. Registra la cantidad de boletos comprados,
 * la fecha de la compra, el costo total, y referencias al tipo de boleto y al usuario comprador.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 *   <li>Registrar información de la transacción de compra (cantidad, fecha, monto total)</li>
 *   <li>Mantener relación con el {@link TipoBoleto} que fue comprado</li>
 *   <li>Asociar la compra con el {@link Usuario} cliente que realizó la transacción</li>
 *   <li>Permitir auditoría y seguimiento de todas las compras realizadas</li>
 * </ul>
 *
 * <p>
 * <strong>Relaciones JPA:</strong>
 * </p>
 * <ul>
 *   <li>Many-to-One con {@link TipoBoleto}: Múltiples compras pueden ser del mismo tipo de boleto</li>
 *   <li>Many-to-One con {@link Usuario}: Un usuario puede realizar múltiples compras</li>
 * </ul>
 *
 * <p>
 * <strong>Cálculo del total:</strong>
 * El campo {@code compraTotal} se calcula como: {@code cantidad × precio del TipoBoleto}
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see TipoBoleto
 * @see Usuario
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compra {

    /**
     * Identificador único de la compra.
     * <p>
     * Generado automáticamente por la base de datos mediante estrategia IDENTITY.
     * Actúa como clave primaria de la tabla de compras.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cantidad de boletos adquiridos en esta transacción.
     * <p>
     * Representa el número de boletos del tipo especificado que el cliente compró.
     * Se almacena en la columna {@code cantidad_comprada} de la base de datos.
     * </p>
     */
    @Column(name = "cantidad_comprada")
    private Integer cantidad;

    /**
     * Fecha y hora en que se realizó la compra.
     * <p>
     * Registra cuándo exactamente se completó la transacción de compra.
     * Útil para auditoría y seguimiento de ventas.
     * Se almacena en la columna {@code fecha_compra} de la base de datos.
     * </p>
     */
    @Column(name = "fecha_compra")
    private LocalDateTime fechaDeCompra;

    /**
     * Monto total de la compra.
     * <p>
     * Representa el costo total de la transacción, calculado como:
     * {@code cantidad × precio unitario del tipo de boleto}.
     * Utiliza {@link BigDecimal} para precisión en operaciones financieras.
     * Se almacena en la columna {@code compra_total} de la base de datos.
     * </p>
     */
    @Column(name = "compra_total")
    private BigDecimal compraTotal;

    /**
     * Tipo de boleto que fue comprado.
     * <p>
     * Relación Many-to-One: Múltiples compras pueden ser del mismo tipo de boleto.
     * Una compra está asociada a exactamente un tipo de boleto.
     * Se almacena mediante la columna {@code tipo_boleto_id} en la base de datos.
     * Permite acceder a información como el precio unitario y el evento asociado.
     * </p>
     *
     * @see TipoBoleto
     */
    @ManyToOne
    @JoinColumn(name = "tipo_boleto_id")
    private TipoBoleto tipoBoleto;

    /**
     * Usuario cliente que realizó la compra.
     * <p>
     * Relación Many-to-One: Un usuario puede realizar múltiples compras.
     * Una compra está asociada a exactamente un usuario comprador.
     * La carga es LAZY para optimizar el rendimiento (se carga bajo demanda).
     * Es un campo obligatorio (no puede ser nulo).
     * Se almacena mediante la columna {@code usuario_id} en la base de datos.
     * </p>
     *
     * @see Usuario
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente;
}
