package com.API.EventFlow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) para operaciones de Compra de Boletos.
 *
 * <p>
 * La clase {@code CompraDTO} se utiliza para transferir datos entre las capas
 * de presentación (API REST) y la capa de lógica de negocio. Es agnóstica respecto
 * a la entidad JPA {@link com.API.EventFlow.model.Compra}, permitiendo una
 * separación clara de responsabilidades y seguridad.
 * </p>
 *
 * <p>
 * <strong>Propósito:</strong>
 * </p>
 * <ul>
 *   <li>Validar datos de compra recibidos desde peticiones HTTP</li>
 *   <li>Transferir información de transacciones entre controlador y servicios</li>
 *   <li>Encapsular la estructura de solicitud para crear/actualizar compras</li>
 *   <li>Proteger la entidad JPA {@code Compra} de cambios no autorizados del cliente</li>
 * </ul>
 *
 * <p>
 * <strong>Casos de uso:</strong>
 * </p>
 * <ul>
 *   <li>Crear nueva compra: POST /api/compras</li>
 *   <li>Actualizar compra: PUT /api/compras/{id}</li>
 * </ul>
 *
 * <p>
 * <strong>Flujo de compra:</strong>
 * </p>
 * <ol>
 *   <li>Cliente envía solicitud con {@code tipoBoletoId} y {@code cantidad}</li>
 *   <li>Servicio válida disponibilidad de boletos</li>
 *   <li>Servicio calcula {@code compraTotal} automáticamente</li>
 *   <li>Servicio registra {@code fechaDeCompra} automáticamente</li>
 *   <li>Cliente recibe respuesta con ID y detalles de compra</li>
 * </ol>
 *
 * <p>
 * <strong>Validaciones:</strong>
 * Se aplican automáticamente mediante anotaciones Jakarta Validation:
 * </p>
 * <ul>
 *   <li>La cantidad de boletos debe ser mínimo 1</li>
 *   <li>El ID del tipo de boleto es obligatorio</li>
 * </ul>
 *
 * <p>
 * <strong>Nota:</strong> El usuario cliente se obtiene del contexto de seguridad
 * (token JWT). Él {@code compraTotal} se calcula automáticamente en el servicio.
 * La {@code fechaDeCompra} se asigna automáticamente con la fecha/hora actual.
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see com.API.EventFlow.model.Compra
 * @see com.API.EventFlow.service.CompraService
 * @see com.API.EventFlow.controller.CompraController
 */
@Getter
@Setter
public class CompraDTO {

    /**
     * Identificador único de la compra.
     * <p>
     * Campo solo de lectura que se incluye en respuestas después de crear/actualizar.
     * No se proporciona en peticiones de creación (se genera automáticamente).
     * </p>
     */
    private Long id;

    /**
     * Cantidad de boletos a comprar.
     * <p>
     * Define cuántos boletos del tipo especificado desea comprar el cliente.
     * Esta cantidad se usa para:
     * <ul>
     *   <li>Validar disponibilidad en el tipo de boleto</li>
     *   <li>Calcular el monto total: {@code cantidad × precio unitario}</li>
     *   <li>Decrementar el stock disponible</li>
     * </ul>
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotNull: Valor obligatorio</li>
     *     <li>@Min: Debe ser mínimo 1 boleto</li>
     *   </ul>
     *
     * @example 1, 5, 25
     */
    @NotNull
    @Min(1)
    private Integer cantidad;

    /**
     * Fecha y hora en que se realizó la compra.
     * <p>
     * Campo solo de lectura que se genera automáticamente en el servidor
     * cuando se procesa la compra. Representa la marca de tiempo (timestamp)
     * de la transacción y se utiliza para auditoría.
     * </p>
     *
     * @example 2026-02-13T15:30:45
     */
    private LocalDateTime fechaDeCompra;

    /**
     * Identificador del tipo de boleto a comprar.
     * <p>
     * Campo de referencia que especifica qué tipo de boleto desea comprar el cliente.
     * Debe corresponder a un tipo de boleto existente y disponible.
     * Se utiliza para:
     * <ul>
     *   <li>Validar disponibilidad de boletos</li>
     *   <li>Recuperar el precio unitario</li>
     *   <li>Obtener información del evento asociado</li>
     * </ul>
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotNull: El ID del tipo de boleto es obligatorio</li>
     *   </ul>
     *
     * @see com.API.EventFlow.model.TipoBoleto
     */
    @NotNull(message = "el ID del tipo boleto es obligatorio")
    private Long tipoBoletoId;

    /**
     * Monto total de la compra.
     * <p>
     * Campo solo de lectura que se calcula automáticamente en el servidor.
     * Representa el costo total de la transacción.
     * Fórmula de cálculo:
     * {@code compraTotal = cantidad × precio unitario del tipo de boleto}
     *
     * Utiliza {@link BigDecimal} para asegurar precisión en operaciones financieras.
     * </p>
     *
     * @example 149.50, 749.75, 5000.00
     */
    private BigDecimal compraTotal;
}
