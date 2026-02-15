package com.API.EventFlow.mapper;

import com.API.EventFlow.dto.TipoBoletoDTO;
import com.API.EventFlow.model.Evento;
import com.API.EventFlow.model.TipoBoleto;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre TipoBoleto (entidad JPA) y TipoBoletoDTO.
 *
 * <p>
 * La clase {@code TipoBoletoMapper} es responsable de transformar datos entre la entidad
 * de persistencia {@link TipoBoleto} y el objeto de transferencia de datos {@link TipoBoletoDTO}.
 * Actúa como intermediaria entre capas permitiendo la separación clara de responsabilidades.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 *   <li>Convertir TipoBoletoDTO → TipoBoleto (entrada desde API)</li>
 *   <li>Convertir TipoBoleto → TipoBoletoDTO (salida hacia API)</li>
 *   <li>Mapear campos automáticamente entre objetos</li>
 *   <li>Manejar referencias complejas (Evento)</li>
 * </ul>
 *
 * <p>
 * <strong>Características especiales:</strong>
 * </p>
 * <ul>
 *   <li>El método convertirDTOaEntidad requiere Evento como parámetro</li>
 *   <li>El método convertirEntidadaDTO extrae ID del Evento</li>
 *   <li>Los campos creadoPor y compras se manejan en el servicio</li>
 * </ul>
 *
 * <p>
 * <strong>Patrón de diseño:</strong> Mapper (también conocido como DTO Mapper o Converter)
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see TipoBoleto
 * @see TipoBoletoDTO
 * @see Evento
 */
@Component
public class TipoBoletoMapper {

    /**
     * Convierte un TipoBoletoDTO a una entidad TipoBoleto.
     *
     * <p>
     * Este método toma los datos recibidos desde la API (TipoBoletoDTO) y los transforma
     * en una entidad JPA (TipoBoleto) preparada para persistencia en base de datos.
     * Requiere que se pase la entidad Evento como parámetro para establecer la relación.
     * </p>
     *
     * <p>
     * <strong>Flujo de conversión:</strong>
     * </p>
     * <ol>
     *   <li>Crea nueva instancia de TipoBoleto</li>
     *   <li>Copia ID de DTO (aunque típicamente será null en creaciones)</li>
     *   <li>Copia boletos disponibles (stock)</li>
     *   <li>Copia nombre del tipo de boleto</li>
     *   <li>Copia precio unitario</li>
     *   <li>Asocia la entidad Evento proporcionada como parámetro</li>
     * </ol>
     *
     * <p>
     * <strong>Campos NO asignados:</strong>
     * </p>
     * <ul>
     *   <li>creadoPor (Usuario) - Debe asignarse en el servicio</li>
     *   <li>Compras (List) - Se inicializa automáticamente vacía</li>
     * </ul>
     *
     * @param tipoBoletoDTO Objeto de transferencia de datos con información del tipo de boleto
     * @param evento Entidad Evento al que pertenecerá el tipo de boleto (no null)
     * @return Entidad TipoBoleto preparada para persistencia
     *
     * @throws NullPointerException si evento es null
     *
     * @example
     * EventoDTO dto = new TipoBoletoDTO();
     * dto.setNombreTipo("VIP");
     * dto.setPrecio(new BigDecimal("149.99"));
     * dto.setBoletosDisponibles(50);
     * Evento evento = eventoRepository.findById(1L);
     * TipoBoleto boleto = mapper.convertirDTOaEntidad(dto, evento);
     * // boleto.getId() = null (será generado)
     * // boleto.getNombreTipo() = "VIP"
     * // boleto.getEvento() = evento
     * // boleto.getCreadoPor() = null (se asignará en servicio)
     */
    public TipoBoleto convertirDTOaEntidad(TipoBoletoDTO tipoBoletoDTO, Evento evento){
        TipoBoleto tipoBoleto = new TipoBoleto();
        tipoBoleto.setId(tipoBoletoDTO.getId());
        tipoBoleto.setBoletosDisponibles(tipoBoletoDTO.getBoletosDisponibles());
        tipoBoleto.setNombreTipo(tipoBoletoDTO.getNombreTipo());
        tipoBoleto.setPrecio(tipoBoletoDTO.getPrecio());
        tipoBoleto.setEvento(evento);
        return tipoBoleto;
    }

    /**
     * Convierte una entidad TipoBoleto a un TipoBoletoDTO.
     *
     * <p>
     * Este método toma una entidad persistida en base de datos (TipoBoleto) y la transforma
     * en un objeto de transferencia de datos (TipoBoletoDTO) preparado para enviar hacia el cliente.
     * </p>
     *
     * <p>
     * <strong>Flujo de conversión:</strong>
     * </p>
     * <ol>
     *   <li>Crea nueva instancia de TipoBoletoDTO</li>
     *   <li>Copia ID del tipo de boleto</li>
     *   <li>Copia boletos disponibles (stock actual)</li>
     *   <li>Copia nombre del tipo de boleto</li>
     *   <li>Copia precio unitario</li>
     *   <li>Extrae ID del Evento asociado (no la entidad completa)</li>
     * </ol>
     *
     * <p>
     * <strong>Transformaciones especiales:</strong>
     * </p>
     * <ul>
     *   <li>evento → eventoId (extrae solo el ID para minimizar datos transferidos)</li>
     * </ul>
     *
     * <p>
     * <strong>Campos NO incluidos:</strong>
     * </p>
     * <ul>
     *   <li>creadoPor (Usuario) - Para no exponer detalles innecesarios</li>
     *   <li>Compras (List) - Lista de compras no incluida en el DTO</li>
     * </ul>
     *
     * @param tipoBoleto Entidad de tipo de boleto desde base de datos
     * @return TipoBoletoDTO preparado para enviar al cliente
     *
     * @example
     * TipoBoleto boleto = tipoBoletoRepository.findById(5L);
     *
     * TipoBoletoDTO dto = mapper.convertirEntidadaDTO(boleto);
     * // dto.getId() = 5
     * // dto.getNombreTipo() = "VIP"
     * // dto.getPrecio() = 149.99
     * // dto.getBoletosDisponibles() = 45 (stock actual)
     * // dto.getEventoId() = 1
     */
    public TipoBoletoDTO convertirEntidadaDTO(TipoBoleto tipoBoleto){
        TipoBoletoDTO tipoBoletoDTO = new TipoBoletoDTO();
        tipoBoletoDTO.setId(tipoBoleto.getId());
        tipoBoletoDTO.setBoletosDisponibles(tipoBoleto.getBoletosDisponibles());
        tipoBoletoDTO.setNombreTipo(tipoBoleto.getNombreTipo());
        tipoBoletoDTO.setPrecio(tipoBoleto.getPrecio());
        tipoBoletoDTO.setEventoId(tipoBoleto.getEvento().getId());
        return tipoBoletoDTO;
    }
}
