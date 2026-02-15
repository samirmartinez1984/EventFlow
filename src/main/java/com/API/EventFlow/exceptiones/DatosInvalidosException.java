package com.API.EventFlow.exceptiones;

/**
 * Excepción lanzada cuando se detectan datos inválidos
 * durante la validación de reglas de negocio.
 *
 * <p>
 * Use esta excepción para indicar que la petición contiene
 * datos que no cumplen las restricciones de negocio (por ejemplo:
 * nombre duplicado, fecha inválida, valores fuera de rango, etc.).
 * </p>
 *
 * <p>
 * El controlador global {@link com.API.EventFlow.exceptiones.GlobalExceptionHandler} la captura y
 * devuelve un HTTP 400 (Bad Request) con el mensaje de error.
 * </p>
 *
 * @see com.API.EventFlow.exceptiones.GlobalExceptionHandler
 */
public class DatosInvalidosException extends RuntimeException{
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
