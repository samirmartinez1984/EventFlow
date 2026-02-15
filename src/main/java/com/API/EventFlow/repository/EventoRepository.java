package com.API.EventFlow.repository;

import com.API.EventFlow.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para acceso a datos de entidad Evento.
 *
 * <p>
 * La interfaz {@code EventoRepository} extiende {@link JpaRepository} y proporciona
 * operaciones CRUD (Create, Read, Update, Delete) automáticas para la entidad {@link Evento}.
 * Además, define métodos personalizados para validaciones de negocio.
 * Spring Data JPA genera automáticamente la implementación en tiempo de ejecución.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades:</strong>
 * </p>
 * <ul>
 *   <li>Proporcionar métodos CRUD básicos: save(), findById(), findAll(), delete()</li>
 *   <li>Ejecutar consultas personalizadas a la base de datos</li>
 *   <li>Validar restricciones de negocio (nombres únicos, etc.)</li>
 *   <li>Mapear resultados de BD a entidades Java</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos heredados de JpaRepository:</strong>
 * </p>
 * <ul>
 *   <li>{@code save(Evento)} - Crear o actualizar un evento</li>
 *   <li>{@code findById(Long)} - Buscar evento por ID</li>
 *   <li>{@code findAll()} - Obtener todos los eventos</li>
 *   <li>{@code delete(Evento)} - Eliminar un evento</li>
 *   <li>{@code count()} - Contar total de eventos</li>
 *   <li>{@code exists(Long)} - Verificar si existe un evento</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos personalizados:</strong>
 * </p>
 * <ul>
 *   <li>{@code existsByNombreEvento(String)} - Verificar si existe evento con ese nombre</li>
 * </ul>
 *
 * <p>
 * <strong>Casos de uso de métodos personalizados:</strong>
 * </p>
 * <ul>
 *   <li>Validar nombres únicos antes de crear/actualizar eventos</li>
 *   <li>Prevenir eventos duplicados</li>
 *   <li>Mantener integridad de datos de negocio</li>
 * </ul>
 *
 * <p>
 * <strong>Posibles mejoras futuras:</strong>
 * </p>
 * <ul>
 *   <li>findByUsuarioId(Long) - Obtener eventos de un usuario</li>
 *   <li>findByFechaEventoBetween(LocalDateTime, LocalDateTime) - Eventos en rango de fechas</li>
 *   <li>findByCapacidadMaximaGreaterThan(Integer) - Eventos con capacidad mínima</li>
 * </ul>
 *
 * <p>
 * <strong>Patrón de diseño:</strong> Repository (parte de Data Access Layer)
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Evento
 * @see JpaRepository
 */
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    /**
     * Verifica si existe un evento con el nombre especificado.
     *
     * <p>
     * Este método es crucial para validar nombres únicos de eventos
     * y prevenir duplicados en la base de datos.
     * </p>
     *
     * <p>
     * <strong>Generación automática:</strong>
     * Spring Data JPA genera la implementación automáticamente analizando
     * el nombre del método:
     * </p>
     * <ul>
     *   <li>{@code existsBy} indica que devuelve Boolean</li>
     *   <li>{@code NombreEvento} especifica el campo a buscar</li>
     * </ul>
     *
     * <p>
     * <strong>Consulta SQL generada:</strong>
     * </p>
     * <pre>
     * SELECT COUNT(*) > 0 FROM eventos
     * WHERE nombre_evento = ?1
     * </pre>
     *
     * @param nombreEvento Nombre del evento a verificar (sensible a mayúsculas)
     * @return {@code true} si existe evento con ese nombre, {@code false} si no existe
     *
     * @example
     * boolean existe = eventoRepository.existsByNombreEvento("Tech Conference 2026");
     * if (existe) {
     *     throw new DatosInválidosException("El nombre del evento ya existe");
     * }
     */
    Boolean existsByNombreEvento(String nombreEvento);

}
