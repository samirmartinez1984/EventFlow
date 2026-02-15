package com.API.EventFlow.repository;

import com.API.EventFlow.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceso a datos de entidad Usuario.
 *
 * <p>
 * La interfaz {@code UsuarioRepository} extiende {@link JpaRepository} y proporciona
 * operaciones CRUD (Create, Read, Update, Delete) automáticas para la entidad {@link Usuario}.
 * Es fundamental para autenticación, autorización y gestión de usuarios.
 * Spring Data JPA genera automáticamente la implementación en tiempo de ejecución.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades:</strong>
 * </p>
 * <ul>
 *   <li>Proporcionar métodos CRUD básicos: save(), findById(), findAll(), delete()</li>
 *   <li>Búsquedas de usuarios por correo (identificador único)</li>
 *   <li>Validar autenticación y autorización</li>
 *   <li>Mantener integridad de datos de usuario</li>
 *   <li>Mapear resultados de BD a entidades Java</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos heredados de JpaRepository:</strong>
 * </p>
 * <ul>
 *   <li>{@code save(Usuario)} - Crear o actualizar un usuario</li>
 *   <li>{@code findById(Long)} - Buscar usuario por ID</li>
 *   <li>{@code findAll()} - Obtener todos los usuarios</li>
 *   <li>{@code delete(Usuario)} - Eliminar un usuario</li>
 *   <li>{@code count()} - Contar total de usuarios</li>
 *   <li>{@code exists(Long)} - Verificar si existe un usuario</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos personalizados:</strong>
 * </p>
 * <ul>
 *   <li>{@code findByCorreo(String)} - Buscar usuario por correo electrónico</li>
 *   <li>{@code existsByCorreo(String)} - Verificar si existe usuario con ese correo</li>
 * </ul>
 *
 * <p>
 * <strong>Integración con Spring Security:</strong>
 * </p>
 * <ul>
 *   <li>El método {@code findByCorreo()} es usado por {@code UserDetailsService}</li>
 *   <li>Permite autenticación mediante correo (username)</li>
 *   <li>Maneja perfiles de usuario basados en Rol (ADMIN, CLIENTE)</li>
 * </ul>
 *
 * <p>
 * <strong>Casos de uso de métodos personalizados:</strong>
 * </p>
 * <ul>
 *   <li>Buscar usuario durante login</li>
 *   <li>Validar correo único durante registro</li>
 *   <li>Verificar existencia de usuario antes de operaciones</li>
 *   <li>Recuperación de contraseña (verificar usuario existe)</li>
 * </ul>
 *
 * <p>
 * <strong>Posibles mejoras futuras:</strong>
 * </p>
 * <ul>
 *   <li>findByRol(Rol) - Obtener usuarios por rol (todo el ADMIN, todos los CLIENTE)</li>
 *   <li>findByNombre(String) - Buscar usuarios por nombre</li>
 *   <li>findAllByRolOrderByNombreAsc(Rol) - Usuarios ordenados por nombre</li>
 * </ul>
 *
 * <p>
 * <strong>Patrón de diseño:</strong> Repository (parte de Data Access Layer)
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Usuario
 * @see JpaRepository
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por correo electrónico.
     *
     * <p>
     * Este método es crucial para autenticación y se utiliza en múltiples contextos:
     * login, creación de eventos, creación de compras, etc.
     * El correo es el identificador único de usuario en el sistema.
     * </p>
     *
     * <p>
     * <strong>Generación automática:</strong>
     * Spring Data JPA genera la implementación automáticamente analizando
     * el nombre del método:
     * </p>
     * <ul>
     *   <li>{@code findBy} indica búsqueda por criterio</li>
     *   <li>{@code Correo} especifica el campo a buscar</li>
     *   <li>{@code Optional<Usuario>} envuelve el resultado</li>
     * </ul>
     *
     * <p>
     * <strong>Consulta SQL generada:</strong>
     * </p>
     * <pre>
     * SELECT * FROM usuarios WHERE correo = ?1 LIMIT 1
     * </pre>
     *
     * <p>
     * <strong>Integración con Spring Security:</strong>
     * Se usa en {@code UserDetailsService.loadUserByUsername()} para autenticación.
     * </p>
     *
     * @param correo Correo electrónico del usuario a buscar (case-sensitive)
     * @return Optional conteniendo el usuario si existe, Optional.empty() si no
     *
     * @throws UsernameNotFoundException (indirectamente a través de UserDetailsService)
     *         si el usuario no existe
     *
     * @example
     * Optional<Usuario> usuario = usuarioRepository.findByCorreo("usuario@example.com");
     * if (usuario.isPresent()) {
     *     System.out.println("Usuario encontrado: " + usuario.get().getNombre());
     * } else {
     *     throw new RecursoNoEncontradoException("Usuario no encontrado");
     * }
     */
    Optional<Usuario> findByCorreo(String correo);

    /**
     * Verifica si existe un usuario con el correo especificado.
     *
     * <p>
     * Este método es utilizado para validar la unicidad del correo durante
     * el registro de nuevos usuarios. Previene correos duplicados en la base de datos.
     * </p>
     *
     * <p>
     * <strong>Generación automática:</strong>
     * Spring Data JPA genera la implementación automáticamente analizando
     * el nombre del método:
     * </p>
     * <ul>
     *   <li>{@code existsBy} indica que devuelve Boolean</li>
     *   <li>{@code Correo} especifica el campo a verificar</li>
     * </ul>
     *
     * <p>
     * <strong>Consulta SQL generada:</strong>
     * </p>
     * <pre>
     * SELECT COUNT(*) > 0 FROM usuarios WHERE correo = ?1
     * </pre>
     *
     * @param correo Correo electrónico a verificar
     * @return {@code true} si existe usuario con ese correo, {@code false} si no existe
     *
     * @example
     * boolean existe = usuarioRepository.existsByCorreo("usuario@example.com");
     * if (existe) {
     *     throw new DatosInválidosException("El correo ya está registrado");
     * }
     * // Proceder con registro de nuevo usuario
     */
    Boolean existsByCorreo(String correo);

}
