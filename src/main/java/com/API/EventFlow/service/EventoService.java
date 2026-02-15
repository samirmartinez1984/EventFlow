package com.API.EventFlow.service;

import com.API.EventFlow.exceptiones.DatosInvalidosException;
import com.API.EventFlow.exceptiones.RecursoNoEncontradoException;
import com.API.EventFlow.dto.EventoDTO;
import com.API.EventFlow.mapper.EventoMapper;
import com.API.EventFlow.model.Evento;
import com.API.EventFlow.model.Usuario;
import com.API.EventFlow.repository.EventoRepository;
import com.API.EventFlow.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para lógica de negocio relacionada con Evento.
 *
 * <p>
 * La clase {@code EventoService} encapsula toda la lógica de negocio para operaciones
 * CRUD de eventos, incluyendo validación de nombres únicos, validación de fechas,
 * y mapeo entre DTO y entidades.
 * Utiliza inyección de dependencias por constructor para acceder a repositorios y mappers.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 *   <li>Listar todos los eventos del sistema</li>
 *   <li>Obtener evento específico por ID</li>
 *   <li>Crear nuevos eventos con validaciones de negocio</li>
 *   <li>Validar nombres únicos de eventos</li>
 *   <li>Validar fechas futuras para eventos</li>
 *   <li>Actualizar eventos existentes</li>
 *   <li>Eliminar eventos del sistema</li>
 *   <li>Mapear entre DTO y entidades</li>
 * </ul>
 *
 * <p>
 * <strong>Validaciones de negocio:</strong>
 * </p>
 * <ul>
 *   <li>Nombre de evento debe ser único</li>
 *   <li>Fecha del evento debe ser posterior a la fecha actual</li>
 *   <li>Usuario creador debe existir en BD</li>
 *   <li>Evento a actualizar/eliminar debe existir en BD</li>
 * </ul>
 *
 * <p>
 * <strong>Transactional:</strong>
 * Los métodos de creación, actualización y eliminación están marcados con {@code @Transactional}
 * para garantizar atomicidad de operaciones en BD.
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Evento
 * @see EventoDTO
 * @see EventoRepository
 * @see EventoMapper
 */
@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene todos los eventos registrados en el sistema.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Consulta BD para obtener todas las entidades Evento</li>
     *   <li>Utiliza streams para mapear cada Evento a EventoDTO</li>
     *   <li>Recolecta resultados en lista</li>
     *   <li>Retorna lista de DTOs al cliente</li>
     * </ol>
     *
     * @return Lista de EventoDTO de todos los eventos en el sistema
     *
     * @example
     * List<EventoDTO> eventos = eventoService.listarTodosLosEventos();
     * eventos.forEach(e -> System.out.println(e.getNombreEvento()));
     */
    public List<EventoDTO> listarTodosLosEventos(){
        List<Evento> eventos = eventoRepository.findAll();
        return eventos.stream()
                .map(eventoMapper::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un evento específico por ID.
     *
     * @param id ID del evento a buscar
     * @return EventoDTO con datos del evento
     * @throws RecursoNoEncontradoException si el evento no existe
     *
     * @example
     * EventoDTO evento = eventoService.obtenerEventoPorId(1L);
     * System.out.println("Evento: " + evento.getNombreEvento());
     */
    public EventoDTO obtenerEventoPorId(Long id){
        Evento eventoEncontrado = eventoRepository.findById(id)
                .orElseThrow(()-> new RecursoNoEncontradoException("Evento no encontrado por el ID " + id));
        return eventoMapper.convertirEntidadaDTO(eventoEncontrado);
    }

    /**
     * Crea un nuevo evento validando nombre único y fecha futura.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Obtiene usuario creador desde BD usando correo</li>
     *   <li>Válida que nombre del evento no sea duplicado</li>
     *   <li>Valida que fecha sea posterior a hoy</li>
     *   <li>Mapea EventoDTO a entidad Evento</li>
     *   <li>Asocia usuario creador al evento</li>
     *   <li>Guarda evento en BD</li>
     *   <li>Mapea entidad creada a DTO y retorna</li>
     * </ol>
     *
     * <p>
     * <strong>Validaciones:</strong>
     * </p>
     * <ul>
     *   <li>Usuario debe existir (RecursoNoEncontradoException)</li>
     *   <li>Nombre debe ser único (DatosInvalidosException)</li>
     *   <li>Fecha debe ser futura (DatosInvalidosException)</li>
     * </ul>
     *
     * @param eventoDTO DTO con datos del nuevo evento
     * @param correo Email del usuario creador (desde token JWT)
     * @return EventoDTO con evento creado incluyendo ID generado
     *
     * @throws RecursoNoEncontradoException si usuario no existe
     * @throws DatosInvalidosException si nombre es duplicado o fecha es pasada
     *
     * @example
     * EventoDTO dto = new EventoDTO();
     * dto.setNombreEvento("Tech Conference 2026");
     * dto.setCapacidadMaxima(500);
     * dto.setFechaEvento(LocalDateTime.of(2026, 12, 31, 20, 30));
     * EventoDTO resultado = eventoService.crearEvento(dto, "admin@example.com");
     * System.out.println("Evento creado con ID: " + resultado.getId());
     */
    @Transactional
    public EventoDTO crearEvento(EventoDTO eventoDTO, String correo){
        // Buscar usuario en la BD por correo.
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe con el correo " + correo));

        // Validar nombre duplicado del evento.
        if (eventoRepository.existsByNombreEvento(eventoDTO.getNombreEvento())){
           throw new DatosInvalidosException("El nombre del evento ya existe");
       }

        // Validar fechas
        if (eventoDTO.getFechaEvento() != null && eventoDTO.getFechaEvento().isBefore(LocalDateTime.now())) {
            throw new DatosInvalidosException("La fecha del evento no puede ser anterior a hoy");
        }

        // Convertir DTO a entidad
       Evento evento = eventoMapper.convertirDTOaEntidad(eventoDTO);

        // Asociar el usuario al evento
        evento.setUsuario(usuario);

        // Guardar el evento en la BD
       Evento eventoCreado = eventoRepository.save(evento);

       // Retornar DTO
        return eventoMapper.convertirEntidadaDTO(eventoCreado);
    }

    /**
     * Actualiza un evento existente.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Busca evento existente por ID</li>
     *   <li>Actualiza campos: nombre, fecha, capacidad</li>
     *   <li>Guarda cambios en BD</li>
     *   <li>Retorna DTO actualizado</li>
     * </ol>
     *
     * <p>
     * <strong>Nota:</strong> El usuario creador NO se puede cambiar.
     * </p>
     *
     * @param eventoDTO DTO con nuevos datos del evento
     * @param id ID del evento a actualizar
     * @return EventoDTO con evento actualizado
     *
     * @throws RecursoNoEncontradoException si evento no existe
     *
     * @example
     * EventoDTO dto = new EventoDTO();
     * dto.setNombreEvento("Tech Conference 2027");
     * dto.setCapacidadMaxima(1000);
     *
     * EventoDTO actualizado = eventoService.actualizarEvento(dto, 1L);
     */
    @Transactional
    public EventoDTO actualizarEvento(EventoDTO eventoDTO, Long id){
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El evento no existe con el ID " + id));

        eventoExistente.setNombreEvento(eventoDTO.getNombreEvento());
        eventoExistente.setFechaEvento(eventoDTO.getFechaEvento());
        eventoExistente.setCapacidadMaxima(eventoDTO.getCapacidadMaxima());

        Evento eventoActualizado = eventoRepository.save(eventoExistente);

        return eventoMapper.convertirEntidadaDTO(eventoActualizado);
    }

    /**
     * Elimina un evento del sistema.
     *
     * <p>
     * <strong>Nota importante:</strong>
     * Debido a las cascadas configuradas en JPA (CascadeType. ALL),
     * eliminar un evento también elimina automáticamente:
     * </p>
     * <ul>
     *   <li>Todos sus tipos de boletos asociados</li>
     *   <li>Todas las compras de esos boletos</li>
     * </ul>
     *
     * @param id ID del evento a eliminar
     * @throws RecursoNoEncontradoException si evento no existe
     *
     * @example
     * eventoService.eliminarEvento(1L);
     * System.out.println("Evento y todos sus datos eliminados");
     */
    @Transactional
    public void eliminarEvento(Long id){
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El evento no se encontró con el ID " + id));
        eventoRepository.delete(evento);
    }
}
