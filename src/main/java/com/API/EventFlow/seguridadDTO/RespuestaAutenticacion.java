package com.API.EventFlow.seguridadDTO;

/**
 * DTO de respuesta para operaciones de autenticación.
 *
 * <p>
 * Contiene el token JWT generado tras un registro o autenticación exitosa.
 * Es devuelto por los endpoints de seguridad y consumido por el cliente para
 * incluir en el header Authorization en futuras peticiones.
 * </p>
 *
 * <p>Campos:</p>
 * <ul>
 *   <li>{@code token} - Token JWT como String</li>
 * </ul>
 *
 * @see com.API.EventFlow.service.AutenticationService
 */
public record RespuestaAutenticacion(String token) {}
