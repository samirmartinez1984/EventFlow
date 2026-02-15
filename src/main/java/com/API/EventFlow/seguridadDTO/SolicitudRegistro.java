package com.API.EventFlow.seguridadDTO;

/**
 * DTO (Data Transfer Object) para la solicitud de registro de un nuevo usuario.
 *
 * <p>
 * Este record representa los datos que el cliente debe enviar al endpoint de registro
 * para crear una nueva cuenta en el sistema. Se utiliza en el servicio de autenticación
 * ({@code AutenticationService}) para construir la entidad {@code Usuario}.
 * </p>
 *
 * <p>Campos:</p>
 * <ul>
 *   <li>{@code nombres} - Nombres del usuario (first name)</li>
 *   <li>{@code apellidos} - Apellidos del usuario</li>
 *   <li>{@code correo} - Correo electrónico único (usado como username)</li>
 *   <li>{@code clave} - Contraseña en texto plano (será encriptada antes de persistir)</li>
 *   <li>{@code rol} - Rol opcional a asignar (ej.: "ADMIN" o "CLIENTE"). En la mayoría de
 *       los casos el servicio asigna {@code CLIENTE} por defecto al registrar usuarios desde la API pública.</li>
 * </ul>
 *
 * <p>
 * Validaciones adicionales (formato de correo, fuerza de contraseña) deben aplicarse
 * en el nivel del controlador o servicio según las políticas del proyecto.
 * </p>
 *
 * @see com.API.EventFlow.service.AutenticationService
 */
public record SolicitudRegistro(String nombres,
                                String apellidos,
                                String correo,
                                String clave,
                                String rol) {}
