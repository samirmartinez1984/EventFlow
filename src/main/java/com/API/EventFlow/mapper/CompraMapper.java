package com.API.EventFlow.mapper;

import com.API.EventFlow.dto.CompraDTO;
import com.API.EventFlow.model.Compra;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper para convertir entre Compra (entidad JPA) y CompraDTO.
 *
 * <p>
 * La clase {@code CompraMapper} es responsable de transformar datos entre la entidad
 * de persistencia {@link Compra} y el objeto de transferencia de datos {@link CompraDTO}.
 * Actúa como intermediaria entre capas de aplicación.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 *   <li>Convertir CompraDTO → Compra (entrada desde API)</li>
 *   <li>Convertir Compra → CompraDTO (salida hacia API)</li>
 *   <li>Mapear campos automáticamente entre objetos</li>
 *   <li>Generar marcas de tiempo para auditoría (fechaDeCompra)</li>
 * </ul>
 *
 * <p>
 * <strong>Patrón de diseño:</strong> Mapper (también conocido como DTO Mapper o Converter)
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Compra
 * @see CompraDTO
 */
@Component
public class CompraMapper {

    /**
     * Convierte un CompraDTO a una entidad Compra.
     *
     * <p>
     * Este método toma los datos recibidos desde la API (CompraDTO) y los transforma
     * en una entidad JPA (Compra) preparada para persistencia en base de datos.
     * </p>
     *
     * <p>
     * <strong>Flujo de conversión:</strong>
     * </p>
     * <ol>
     *   <li>Crea nueva instancia de Compra</li>
     *   <li>Copia ID de DTO (aunque typically será null en creaciones)</li>
     *   <li>Genera fecha actual con {@code LocalDateTime.now()}</li>
     *   <li>Copia cantidad de boletos</li>
     *   <li>Copia total de la compra (calculado previamente)</li>
     * </ol>
     *
     * <p>
     * <strong>Nota importante:</strong> El campo {@code tipoBoleto} y {@code cliente}
     * NO se asignan aquí. Deben ser asignados en el servicio después de validaciones.
     * </p>
     *
     * @param compraDTO Objeto de transferencia de datos con información de compra
     * @return Entidad Compra preparada para persistencia
     *
     * @example
     * CompraDTO dto = new CompraDTO();
     * dto.setCantidad(2);
     * dto.setCompraTotal(new BigDecimal("299.98"));
     *
     * Compra compra = mapper.convertirDTOaEntidad(dto);
     * // compra.getFechaDeCompra() = ahora
     * // compra.getCantidad() = 2
     */
    public Compra convertirDTOaEntidad(CompraDTO compraDTO){
        Compra compra = new Compra();
        compra.setId(compraDTO.getId());
        compra.setFechaDeCompra(LocalDateTime.now());
        compra.setCantidad(compraDTO.getCantidad());
        compra.setCompraTotal(compraDTO.getCompraTotal());
        return compra;
    }

    /**
     * Convierte una entidad Compra a un CompraDTO.
     *
     * <p>
     * Este método toma una entidad persistida en base de datos (Compra) y la transforma
     * en un objeto de transferencia de datos (CompraDTO) preparado para enviar hacia el cliente.
     * </p>
     *
     * <p>
     * <strong>Flujo de conversión:</strong>
     * </p>
     * <ol>
     *   <li>Crea nueva instancia de CompraDTO</li>
     *   <li>Copia ID de la compra</li>
     *   <li>Copia fecha de compra (auditoría)</li>
     *   <li>Copia cantidad de boletos</li>
     *   <li>Extrae ID del tipo de boleto asociado</li>
     *   <li>Copia monto total pagado</li>
     * </ol>
     *
     * <p>
     * <strong>Transformaciones especiales:</strong>
     * </p>
     * <ul>
     *   <li>tipoBoleto → tipoBoletoId (extrae solo el ID, no la entidad completa)</li>
     * </ul>
     *
     * @param compra Entidad de compra desde base de datos
     * @return CompraDTO preparado para enviar al cliente
     *
     * @example
     * Compra compra = compraRepository.findById(1L);
     * CompraDTO dto = mapper.convertirEntidadaDTO(compra);
     * // dto.getId() = 1
     * // dto.getTipoBoletoId() = 5 (ID del tipo de boleto)
     * // dto.getFechaDeCompra() = 2026-02-13T15:30:45
     */
    public CompraDTO convertirEntidadaDTO(Compra compra){
        CompraDTO compraDTO = new CompraDTO();
        compraDTO.setId(compra.getId());
        compraDTO.setFechaDeCompra(compra.getFechaDeCompra());
        compraDTO.setCantidad(compra.getCantidad());
        compraDTO.setTipoBoletoId(compra.getTipoBoleto().getId());
        compraDTO.setCompraTotal(compra.getCompraTotal());
        return compraDTO;
    }
}
