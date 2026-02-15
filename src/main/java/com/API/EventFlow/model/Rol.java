package com.API.EventFlow.model;

/**
 * Enumeración que define los roles de usuario disponibles en el sistema EventFlow.
 *
 * <p>
 * La enumeración {@code Rol} establece los diferentes permisos y niveles de acceso
 * que puede tener un usuario dentro del sistema. Cada usuario debe ser asignado a
 * exactamente uno de estos roles.
 * </p>
 *
 * <p>
 * <strong>Roles disponibles:</strong>
 * </p>
 * <ul>
 *   <li>{@code CLIENTE}: Usuario final que compra boletos para eventos</li>
 *   <li>{@code ADMIN}: Usuario administrador con permisos de gestión completa</li>
 * </ul>
 *
 * <p>
 * <strong>Uso en el sistema:</strong>
 * </p>
 * <ul>
 *   <li>Determina los permisos y operaciones disponibles para cada usuario</li>
 *   <li>Se utiliza en validaciones de seguridad y control de acceso</li>
 *   <li>Asociado con la entidad {@link Usuario} mediante anotación {@code @Enumerated}</li>
 * </ul>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Usuario
 */
public enum Rol {
    /**
     * Rol de usuario cliente.
     * <p>
     * Permisos: Comprar boletos, ver eventos, consultar historial de compras.
     * Restricciones: No puede crear eventos, ni gestionar tipos de boletos,
     * ni acceder a funciones administrativas.
     * </p>
     */
    CLIENTE,

    /**
     * Rol de usuario administrador.
     * <p>
     * Permisos: Crear eventos, gestionar tipos de boletos, editar eventos,
     * ver reportes, gestionar usuarios (si corresponde).
     * Características: Acceso completo a todas las funcionalidades del sistema.
     * </p>
     */
    ADMIN

}
