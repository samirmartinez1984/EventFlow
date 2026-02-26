package com.API.EventFlow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) para operaciones de Compra de Boletos.
 */
@Getter
@Setter
public class CompraDTO {

    /**
     * Identificador único de la compra.
     */
    private Long id;

    /**
     * Cantidad de boletos a comprar.
     */
    @NotNull
    @Min(1)
    private Integer cantidad;

    /**
     * Fecha y hora en que se realizó la compra.
     */
    private LocalDateTime fechaDeCompra;

    /**
     * Identificador del tipo de boleto a comprar.
     */
    @NotNull(message = "el ID del tipo boleto es obligatorio")
    private Long tipoBoletoId;

    /**
     * Monto total de la compra.
     */
    private BigDecimal compraTotal;

    /**
     * URL de la factura electrónica generada.
     * Puede ser nulo si la factura aún no está disponible.
     */
    private String facturaUrl;
}
