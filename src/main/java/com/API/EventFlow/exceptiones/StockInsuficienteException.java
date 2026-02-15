package com.API.EventFlow.exceptiones;

/**
 * Excepción lanzada cuando no hay suficiente stock de boletos
 * para completar una operación de compra o actualización.
 *
 * <p>
 * Se lanza normalmente desde servicios que gestionan compras o stock
 * (por ejemplo: {@code CompraService}) cuando la cantidad solicitada
 * excede la disponibilidad.
 * </p>
 *
 * <p>
 * El controlador global {@link GlobalExceptionHandler} puede capturarla
 * y devolver un HTTP 400 (Bad Request) con el mensaje apropiado.
 * </p>
 */
public class StockInsuficienteException extends RuntimeException{
    public StockInsuficienteException(String mensaje) {
       super(mensaje);
    }
}
