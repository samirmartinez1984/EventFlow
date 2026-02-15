package com.API.EventFlow.exceptiones;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la base de datos.
 *
 * <p>
 * Se usa para indicar que una búsqueda por ID u otro criterio no devolvió resultados
 * (por ejemplo: evento, tipo de boleto, compra, usuario, etc.).
 * </p>
 *
 * <p>
 * El controlador global {@link GlobalExceptionHandler} la captura y
 * devuelve un HTTP 404 (Not Found) con el mensaje de error.
 * </p>
 *
 * @see GlobalExceptionHandler
 */
public class RecursoNoEncontradoException extends RuntimeException{
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
