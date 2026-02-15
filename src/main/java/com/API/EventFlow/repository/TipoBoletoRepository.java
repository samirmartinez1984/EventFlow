package com.API.EventFlow.repository;

import com.API.EventFlow.model.TipoBoleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para acceso a datos de entidad TipoBoleto.
 *
 * <p>
 * La interfaz {@code TipoBoletoRepository} extiende {@link JpaRepository} y proporciona
 * operaciones CRUD (Create, Read, Update, Delete) automáticas para la entidad {@link TipoBoleto}.
 * Además, define métodos personalizados para validaciones de eventos.
 * Spring Data JPA genera automáticamente la implementación en tiempo de ejecución.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades:</strong>
 * </p>
 * <ul>
 *   <li>Proporcionar métodos CRUD básicos: save(), findById(), findAll(), delete()</li>
 *   <li>Ejecutar consultas personalizadas a la base de datos</li>
 *   <li>Validar relaciones con eventos</li>
 *   <li>Mapear resultados de BD a entidades Java</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos heredados de JpaRepository:</strong>
 * </p>
 * <ul>
 *   <li>{@code save(TipoBoleto)} - Crear o actualizar un tipo de boleto</li>
 *   <li>{@code findById(Long)} - Buscar tipo de boleto por ID</li>
 *   <li>{@code findAll()} - Obtener todos los tipos de boletos</li>
 *   <li>{@code delete(TipoBoleto)} - Eliminar un tipo de boleto</li>
 *   <li>{@code count()} - Contar total de tipos de boletos</li>
 *   <li>{@code exists(Long)} - Verificar si existe un tipo de boleto</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos personalizados:</strong>
 * </p>
 * <ul>
 *   <li>{@code existsByEventoId(Long)} - Verificar si existen tipos de boleto para un evento</li>
 * </ul>
 *
 * <p>
 * <strong>Casos de uso de métodos personalizados:</strong>
 * </p>
 * <ul>
 *   <li>Validar que un evento tenga tipos de boleto antes de permitir compras</li>
 *   <li>Verificar integridad referencial</li>
 *   <li>Obtener información sobre disponibilidad de tipos de boleto para un evento</li>
 * </ul>
 *
 * <p>
 * <strong>Posibles mejoras futuras:</strong>
 * </p>
 * <ul>
 *   <li>findByEventoId(Long) - Obtener todos los tipos de boleto de un evento</li>
 *   <li>findByNombreTipo(String) - Buscar por nombre de tipo</li>
 *   <li>findByPrecioBetween(BigDecimal, BigDecimal) - Tipos de boleto por rango de precio</li>
 *   <li>findByBoletosDisponiblesGreaterThan(Integer) - Tipos con boletos disponibles</li>
 * </ul>
 *
 * <p>
 * <strong>Patrón de diseño:</strong> Repository (parte de Data Access Layer)
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see TipoBoleto
 * @see JpaRepository
 */
@Repository
public interface TipoBoletoRepository extends JpaRepository<TipoBoleto, Long> {

    /**
     * Verifica si existen tipos de boleto para un evento específico.
     *
     * <p>
     * Este método valida que un evento tenga al menos un tipo de boleto disponible.
     * Es útil para verificar integridad referencial y disponibilidad de boletos.
     * </p>
     *
     * <p>
     * <strong>Generación automática:</strong>
     * Spring Data JPA genera la implementación automáticamente analizando
     * el nombre del método:
     * </p>
     * <ul>
     *   <li>{@code existsBy} indica que devuelve Boolean</li>
     *   <li>{@code EventoId} especifica el campo a buscar (relación Many-to-One)</li>
     * </ul>
     *
     * <p>
     * <strong>Consulta SQL generada:</strong>
     * </p>
     * <pre>
     * SELECT COUNT(*) > 0 FROM tipo_boletos
     * WHERE evento_id = ?1
     * </pre>
     *
     * @param eventoId ID del evento a verificar
     * @return {@code true} si existen tipos de boleto para ese evento, {@code false} si no
     *
     * @example
     * boolean tiene boletos = tipoBoletoRepository.existsByEventoId(1L);
     * if (tiene boletos) {
     *     // El evento tiene tipos de boleto disponibles
     *     List<TipoBoleto> boletos = tipoBoletoRepository.findByEventoId(1L);
     * } else {
     *     throw new RecursoNoEncontradoException("El evento no tiene tipos de boleto");
     * }
     */
    Boolean existsByEventoId(Long eventoId);
}
