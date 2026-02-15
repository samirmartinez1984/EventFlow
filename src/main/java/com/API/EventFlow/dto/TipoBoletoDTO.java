package com.API.EventFlow.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) para operaciones de Tipo de Boleto.
 *
 * <p>
 * La clase {@code TipoBoletoDTO} se utiliza para transferir datos entre las capas
 * de presentación (API REST) y la capa de lógica de negocio. Es agnóstica respecto
 * a la entidad JPA {@link com.API.EventFlow.model.TipoBoleto}, permitiendo una
 * separación clara de responsabilidades.
 * </p>
 *
 * <p>
 * <strong>Propósito:</strong>
 * </p>
 * <ul>
 *   <li>Validar datos recibidos desde el cliente (peticiones HTTP)</li>
 *   <li>Transformar datos de entrada para ser procesados por servicios</li>
 *   <li>Encapsular la estructura de respuesta para crear/actualizar tipos de boletos</li>
 *   <li>Proteger la entidad JPA de cambios no autorizados del cliente</li>
 * </ul>
 *
 * <p>
 * <strong>Casos de uso:</strong>
 * </p>
 * <ul>
 *   <li>Crear nuevo tipo de boleto: POST /api/tipo-boletos</li>
 *   <li>Actualizar tipo de boleto: PUT /api/tipo-boletos/{id}</li>
 * </ul>
 *
 * <p>
 * <strong>Validaciones:</strong>
 * Se aplican automáticamente mediante anotaciones Jakarta Validation:
 * </p>
 * <ul>
 *   <li>El nombre no puede estar vacío</li>
 *   <li>El precio debe ser mayor a 0 con máximo 2 decimales</li>
 *   <li>La cantidad disponible debe ser mínimo 1 boleto</li>
 *   <li>El ID del evento es obligatorio para asociar el boleto</li>
 * </ul>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see com.API.EventFlow.model.TipoBoleto
 * @see com.API.EventFlow.service.TipoBoletoService
 * @see com.API.EventFlow.controller.TipoBoletoController
 */
@Getter
@Setter
public class TipoBoletoDTO {

    /**
     * Identificador único del tipo de boleto.
     * <p>
     * Campo solo de lectura que se incluye en respuestas después de crear/actualizar.
     * No se proporciona en peticiones de creación (se genera automáticamente).
     * </p>
     */
    private Long id;

    /**
     * Nombre o categoría del tipo de boleto.
     * <p>
     * Representa la denominación de la categoría (ej: "VIP", "General", "Estudiante").
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotBlank: No puede estar vacío o contener solo espacios en blanco</li>
     *   </ul>
     */
    @NotBlank(message = "El nombre del tipo boleto no puede estar vacio")
    private String nombreTipo;

    /**
     * Precio unitario del boleto.
     * <p>
     * Valor monetario de cada boleto expresado en la moneda del sistema (ej: USD, EUR).
     * Se utiliza para calcular el monto total de las compras.
     * Utiliza {@link BigDecimal} para asegurar precisión en operaciones financieras.
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotNull: Valor obligatorio</li>
     *     <li>@DecimalMin: Debe ser estrictamente mayor que 0.00</li>
     *     <li>@Digits: Máximo 6 dígitos enteros y 2 decimales (ej: 999999.99)</li>
     *   </ul>
     *
     * @example 29.99, 149.50, 5000.00
     */
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 6, fraction = 2, message = "Formato de precio inválido")
    private BigDecimal precio;

    /**
     * Cantidad de boletos disponibles para la venta.
     * <p>
     * Define cuántos boletos de este tipo están inicialmente disponibles para comprar.
     * Esta cantidad disminuye conforme se realizan compras.
     * Debe ser al menos 1 boleto para que sea válido.
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotNull: Valor obligatorio</li>
     *     <li>@Min: Debe ser mínimo 1 boleto</li>
     *   </ul>
     *
     * @example 50, 100, 500
     */
    @NotNull
    @Min(1)
    private Integer boletosDisponibles;

    /**
     * Identificador del evento al que pertenece este tipo de boleto.
     * <p>
     * Campo de referencia que establece la relación con el evento.
     * Se utiliza para asociar el tipo de boleto a un evento específico.
     * Debe corresponder a un evento existente en la base de datos.
     * </p>
     *
     * @validations
     *   <ul>
     *     <li>@NotNull: El ID del evento es obligatorio</li>
     *   </ul>
     *
     * @see com.API.EventFlow.model.Evento
     */
    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;
}
