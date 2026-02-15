package com.API.EventFlow.seguridadDTO;

/**
 * DTO para la solicitud de autenticación (login).
 *
 * <p>
 * Representa las credenciales que el cliente envía para autenticarse en el sistema.
 * Se utiliza en {@code AutenticationService.autenticar(...)} y en el controlador de seguridad.
 * </p>
 *
 * <p>Campos:</p>
 * <ul>
 *   <li>{@code correo} - Email del usuario (username)</li>
 *   <li>{@code clave} - Contraseña en texto plano</li>
 * </ul>
 *
 * @see com.API.EventFlow.service.AutenticationService(SolicitudAutenticacion)
 */
public record SolicitudAutenticacion(String correo, String clave) {}
