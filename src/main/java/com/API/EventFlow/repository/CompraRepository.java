package com.API.EventFlow.repository;

import com.API.EventFlow.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para acceso a datos de entidad Compra.
 *
 * <p>
 * La interfaz {@code CompraRepository} extiende {@link JpaRepository} y proporciona
 * operaciones CRUD (Create, Read, Update, Delete) automáticas para la entidad {@link Compra}.
 * Spring Data JPA genera automáticamente la implementación en tiempo de ejecución.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades:</strong>
 * </p>
 * <ul>
 *   <li>Proporcionar métodos CRUD básicos: save(), findById(), findAll(), delete()</li>
 *   <li>Ejecutar consultas a la base de datos</li>
 *   <li>Mapear resultados de BD a entidades Java</li>
 *   <li>Manejar transacciones automáticamente</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos heredados de JpaRepository:</strong>
 * </p>
 * <ul>
 *   <li>{@code save(Compra)} - Crear o actualizar una compra</li>
 *   <li>{@code findById(Long)} - Buscar compra por ID</li>
 *   <li>{@code findAll()} - Obtener todas las compras</li>
 *   <li>{@code delete(Compra)} - Eliminar una compra</li>
 *   <li>{@code count()} - Contar total de compras</li>
 *   <li>{@code exists(Long)} - Verificar si existe una compra</li>
 * </ul>
 *
 * <p>
 * <strong>Métodos personalizados:</strong>
 * Actualmente, no hay métodos personalizados. Se pueden agregar si es necesario, ej.:
 * </p>
 * <ul>
 *   <li>findByClienteId(Long usuarioId) - Obtener compras de un usuario</li>
 *   <li>findByFechaDeCompraBetween(LocalDateTime inicio, LocalDateTime fin) - Compras por rango de fechas</li>
 * </ul>
 *
 * <p>
 * <strong>Patrón de diseño:</strong> Repository (parte de Data Access Layer)
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Compra
 * @see JpaRepository
 */
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    // No hay métodos personalizados actualmente.
    // Los métodos CRUD básicos son heredados de JpaRepository.
}
