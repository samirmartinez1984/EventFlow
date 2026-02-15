package com.API.EventFlow.mapper;

import com.API.EventFlow.dto.EventoDTO;
import com.API.EventFlow.model.Evento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper para convertir entre Evento (entidad JPA) y EventoDTO.
 *
 * <p>
 * La clase {@code EventoMapper} es responsable de transformar datos entre la entidad
 * de persistencia {@link Evento} y el objeto de transferencia de datos {@link EventoDTO}.
 * Actúa como intermediaria entre capas de aplicación permitiendo la separación de responsabilidades.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 *   <li>Convertir EventoDTO → Evento (entrada desde API)</li>
 *   <li>Convertir Evento → EventoDTO (salida hacia API)</li>
 *   <li>Mapear campos automáticamente entre objetos</li>
 *   <li>Manejar conversiones especiales de fechas</li>
 * </ul>
 *
 * <p>
 * <strong>Patrón de diseño:</strong> Mapper (también conocido como DTO Mapper o Converter)
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Evento
 * @see EventoDTO
 */
@Component
public class EventoMapper {

    /**
     * Convierte un EventoDTO a una entidad Evento.
     *
     * <p>
     * Este método toma los datos recibidos desde la API (EventoDTO) y los transforma
     * en una entidad JPA (Evento) preparada para persistencia en base de datos.
     * </p>
     *
     * <p>
     * <strong>Flujo de conversión:</strong>
     * </p>
     * <ol>
     *   <li>Crea nueva instancia de Evento</li>
     *   <li>Copia ID de DTO (aunque typically será null en creaciones)</li>
     *   <li>Copia nombre del evento</li>
     *   <li>Genera fecha actual con {@code LocalDateTime.now()}</li>
     *   <li>Copia capacidad máxima de asistentes</li>
     * </ol>
     *
     * <p>
     * <strong>⚠️ Nota importante:</strong> El campo {@code usuario} (creador del evento)
     * NO se asigna aquí. Debe ser asignado en el servicio después de obtener el usuario
     * del contexto de seguridad.
     * </p>
     *
     * <p>
     * <strong>Actualización de fecha:</strong>
     * La fecha del evento se establece a {@code LocalDateTime.now()} en lugar de usar
     * la fecha del DTO. Esto se debe a un bug potencial que será corregido.
     * </p>
     *
     * @param eventoDTO Objeto de transferencia de datos con información del evento
     * @return Entidad Evento preparada para persistencia
     *
     * @example
     * EventoDTO dto = new EventoDTO();
     * dto.setNombreEvento("Tech Conference 2026");
     * dto.setCapacidadMaxima(500);
     * dto.setFechaEvento(LocalDateTime.of(2026, 12, 31, 20, 30));
     * Evento evento = mapper.convertirDTOaEntidad(dto);
     * // evento.getId() = null (será generado)
     * // evento.getNombreEvento() = "Tech Conference 2026"
     * // evento.getFechaEvento() = ahora (BUG: debería ser la fecha del DTO)
     * // evento.getCapacidadMaxima() = 500
     */
    public Evento convertirDTOaEntidad(EventoDTO eventoDTO){
        Evento evento = new Evento();
        evento.setId(eventoDTO.getId());
        evento.setNombreEvento(eventoDTO.getNombreEvento());
        // ⚠️ BUG POTENCIAL: Se ignora la fecha del DTO y se usa LocalDateTime.now()
        // TODO: Corregir para usar: evento.setFechaEvento(eventoDTO.getFechaEvento());
        evento.setFechaEvento(LocalDateTime.now());
        evento.setCapacidadMaxima(eventoDTO.getCapacidadMaxima());
        return evento;
    }

    /**
     * Convierte una entidad Evento a un EventoDTO.
     *
     * <p>
     * Este método toma una entidad persistida en base de datos (Evento) y la transforma
     * en un objeto de transferencia de datos (EventoDTO) preparado para enviar hacia el cliente.
     * </p>
     *
     * <p>
     * <strong>Flujo de conversión:</strong>
     * </p>
     * <ol>
     *   <li>Crea nueva instancia de EventoDTO</li>
     *   <li>Copia ID del evento</li>
     *   <li>Copia nombre del evento</li>
     *   <li>Copia fecha del evento</li>
     *   <li>Copia capacidad máxima</li>
     * </ol>
     *
     * <p>
     * <strong>Campos NO incluidos:</strong>
     * </p>
     * <ul>
     *   <li>Usuario (creador) - Para no exponer detalles innecesarios</li>
     *   <li>tipoBoletos (lista de boletos) - Para no exponer relaciones complejas</li>
     * </ul>
     *
     * @param evento Entidad de evento desde base de datos
     * @return EventoDTO preparado para enviar al cliente
     *
     * @example
     * Evento evento = eventoRepository.findById(1L);
     * EventoDTO dto = mapper.convertirEntidadaDTO(evento);
     * // dto.getId() = 1
     * // dto.getNombreEvento() = "Tech Conference 2026"
     * // dto.getFechaEvento() = 2026-12-31T20:30:00
     * // dto.getCapacidadMaxima() = 500
     */
    public EventoDTO convertirEntidadaDTO(Evento evento){
        EventoDTO eventoDTO = new EventoDTO();
        eventoDTO.setId(evento.getId());
        eventoDTO.setNombreEvento(evento.getNombreEvento());
        eventoDTO.setFechaEvento(evento.getFechaEvento());
        eventoDTO.setCapacidadMaxima(evento.getCapacidadMaxima());
        return eventoDTO;

    }
}
