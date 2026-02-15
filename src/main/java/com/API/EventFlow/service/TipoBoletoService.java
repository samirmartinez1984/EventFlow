package com.API.EventFlow.service;

import com.API.EventFlow.exceptiones.RecursoNoEncontradoException;
import com.API.EventFlow.dto.TipoBoletoDTO;
import com.API.EventFlow.mapper.TipoBoletoMapper;
import com.API.EventFlow.model.Evento;
import com.API.EventFlow.model.TipoBoleto;
import com.API.EventFlow.model.Usuario;
import com.API.EventFlow.repository.EventoRepository;
import com.API.EventFlow.repository.TipoBoletoRepository;
import com.API.EventFlow.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para lógica de negocio relacionada con TipoBoleto.
 *
 * <p>
 * La clase {@code TipoBoletoService} encapsula toda la lógica de negocio para operaciones
 * CRUD de tipos de boletos, incluyendo creación, actualización, eliminación y listado.
 * Un tipo de boleto representa una categoría específica de boletos para un evento (ej.: VIP, General).
 * Utiliza inyección de dependencias por constructor para acceder a repositorios y mappers.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TipoBoletoService {

    private final TipoBoletoRepository tipoBoletoRepository;
    private final TipoBoletoMapper tipoBoletoMapper;
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo tipo de boleto para un evento específico.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Valida que evento existe en BD</li>
     *   <li>Obtiene usuario creador desde BD usando correo</li>
     *   <li>Mapea TipoBoletoDTO a entidad TipoBoleto</li>
     *   <li>Asocia usuario creador al tipo de boleto</li>
     *   <li>Guarda tipo de boleto en BD</li>
     *   <li>Mapea entidad creada a DTO y retorna</li>
     * </ol>
     *
     * <p>
     * <strong>Validaciones:</strong>
     * </p>
     * <ul>
     *   <li>Evento debe existir (RecursoNoEncontradoException)</li>
     *   <li>Usuario debe existir (RecursoNoEncontradoException)</li>
     * </ul>
     *
     * @param tipoBoletoDTO DTO con datos del nuevo tipo de boleto
     * @param correo Email del usuario administrador creador (desde token JWT)
     * @return TipoBoletoDTO con tipo de boleto creado incluyendo ID generado
     *
     * @throws RecursoNoEncontradoException si evento o usuario no existen
     *
     * @example
     * TipoBoletoDTO dto = new TipoBoletoDTO();
     * dto.setNombreTipo("VIP");
     * dto.setPrecio(new BigDecimal("149.99"));
     * dto.setBoletosDisponibles(50);
     * dto.setEventoId(1L);
     * TipoBoletoDTO resultado = tipoBoletoService.crearTipoBoleto(dto, "admin@example.com");
     * System.out.println("Tipo de boleto creado con ID: " + resultado.getId());
     */
    @Transactional
    public TipoBoletoDTO crearTipoBoleto(TipoBoletoDTO tipoBoletoDTO, String correo){

        // Buscamos el evento en bd
        Evento evento = eventoRepository.findById(tipoBoletoDTO.getEventoId())
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("El evento no se encuentra con el ID " + tipoBoletoDTO.getEventoId()));

        // Buscamos el usuario creador en bd.
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("El usuario no existe con el correo" + correo));

        // Convertimos el tipo boletoDTO a entidad
        TipoBoleto tipoBoleto = tipoBoletoMapper.convertirDTOaEntidad(tipoBoletoDTO, evento);

        // Asociar el usuario al tipo boleto.
        tipoBoleto.setCreadoPor(usuario);

        // Guardar el tipo boleto creado en la bd
        TipoBoleto tipoBoletoCreado = tipoBoletoRepository.save(tipoBoleto);

        // Convertir el tipo boleto creado de entidad a DTO
        return tipoBoletoMapper.convertirEntidadaDTO(tipoBoletoCreado);
    }

    /**
     * Obtiene todos los tipos de boleto registrados en el sistema.
     *
     * <p>
     * <strong>Nota:</strong> Este método lanza excepción si no hay tipos de boleto.
     * En producción, considerar devolver lista vacía en lugar de excepción.
     * </p>
     *
     * @return Lista de TipoBoletoDTO de todos los tipos de boleto
     * @throws RecursoNoEncontradoException si no hay tipos de boleto en el sistema
     *
     * @example
     * List<TipoBoletoDTO> boletos = tipoBoletoService.listarTodosLosBoletos();
     * System.out.println("Total de tipos: " + boletos.size());
     */
    public List<TipoBoletoDTO> listarTodosLosBoletos(){
        List<TipoBoleto> tipoBoletos = tipoBoletoRepository.findAll();
        if (tipoBoletos.isEmpty()){
            throw new RecursoNoEncontradoException("La lista de tipos de boletos no puede estar vacía");
        }
        return tipoBoletos.stream().map(tipoBoletoMapper::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un tipo de boleto específico por ID.
     *
     * @param id ID del tipo de boleto a buscar
     * @return TipoBoletoDTO con datos del tipo de boleto
     * @throws RecursoNoEncontradoException si el tipo de boleto no existe
     *
     * @example
     * TipoBoletoDTO boleto = tipoBoletoService.obtenerTipoBoletoPorId(5L);
     * System.out.println("Tipo: " + boleto.getNombreTipo());
     */
    public TipoBoletoDTO obtenerTipoBoletoPorId(Long id){
        TipoBoleto tipoBoletoBuscado = tipoBoletoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El tipo de boleto no existe con el ID" + id));
        return tipoBoletoMapper.convertirEntidadaDTO(tipoBoletoBuscado);
    }

    /**
     * Actualiza un tipo de boleto existente.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Busca tipo de boleto existente por ID</li>
     *   <li>Válida que evento nuevo existe</li>
     *   <li>Actualiza campos: nombre, precio, stock, evento</li>
     *   <li>Guarda cambios en BD</li>
     *   <li>Retorna DTO actualizado</li>
     * </ol>
     *
     * <p>
     * <strong>Nota:</strong> El usuario creador (creadoPor) NO se puede cambiar.
     * </p>
     *
     * @param tipoBoletoDTO DTO con nuevos datos del tipo de boleto
     * @param id ID del tipo de boleto a actualizar
     * @return TipoBoletoDTO con tipo de boleto actualizado
     *
     * @throws RecursoNoEncontradoException si tipo de boleto o evento no existen
     *
     * @example
     * TipoBoletoDTO dto = new TipoBoletoDTO();
     * dto.setNombreTipo("VIP Premium");
     * dto.setBoletosDisponibles(100);
     *
     * TipoBoletoDTO actualizado = tipoBoletoService.actualizarTipoBoleto(dto, 5L);
     */
    @Transactional
    public TipoBoletoDTO actualizarTipoBoleto(TipoBoletoDTO tipoBoletoDTO, Long id){
         TipoBoleto tipoBoletoExistente = tipoBoletoRepository.findById(id)
                 .orElseThrow(() -> new RecursoNoEncontradoException("El tipo boleto no existe con el ID " + id));
        Evento eventoExistente = eventoRepository.findById(tipoBoletoDTO.getEventoId())
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("El evento no existe con el ID " + tipoBoletoDTO.getEventoId()));
         tipoBoletoExistente.setNombreTipo(tipoBoletoDTO.getNombreTipo());
         tipoBoletoExistente.setBoletosDisponibles(tipoBoletoDTO.getBoletosDisponibles());
         tipoBoletoExistente.setPrecio(tipoBoletoDTO.getPrecio());
         tipoBoletoExistente.setEvento(eventoExistente);
         TipoBoleto boletoActualizado = tipoBoletoRepository.save(tipoBoletoExistente);
         return tipoBoletoMapper.convertirEntidadaDTO(boletoActualizado);
    }

    /**
     * Elimina un tipo de boleto del sistema.
     *
     * <p>
     * <strong>Nota importante:</strong>
     * Debido a las cascadas configuradas en JPA (CascadeType. ALL),
     * eliminar un tipo de boleto también elimina automáticamente:
     * </p>
     * <ul>
     *   <li>Todas las compras de ese tipo de boleto</li>
     * </ul>
     *
     * @param id ID del tipo de boleto a eliminar
     * @throws RecursoNoEncontradoException si el tipo de boleto no existe
     *
     * @example
     * tipoBoletoService.eliminarBoleto(5L);
     * System.out.println("Tipo de boleto y compras asociadas eliminados");
     */
    @Transactional
    public void eliminarBoleto(Long id){
        TipoBoleto tipoBoleto = tipoBoletoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El boleto no existe con el ID " + id));
        tipoBoletoRepository.delete(tipoBoleto);
    }
}
