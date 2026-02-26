package com.API.EventFlow.seguridadDTO;

/**
 * DTO (Data Transfer Object) para la solicitud de registro de un nuevo usuario.
 *
 * <p>
 * Este record representa los datos que el cliente debe enviar al endpoint de registro
 * para crear una nueva cuenta en el sistema. Se utiliza en el servicio de autenticación
 * ({@code AutenticationService}) para construir la entidad {@code Usuario}.
 * </p>
 */
public record SolicitudRegistro(
    String nombres,
    String apellidos,
    String cedula,
    String correo,
    String clave,
    String rol
) {}
