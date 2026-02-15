package com.API.EventFlow.service;

import com.API.EventFlow.exceptiones.RecursoNoEncontradoException;
import com.API.EventFlow.exceptiones.StockInsuficienteException;
import com.API.EventFlow.dto.CompraDTO;
import com.API.EventFlow.mapper.CompraMapper;
import com.API.EventFlow.model.Compra;
import com.API.EventFlow.model.TipoBoleto;
import com.API.EventFlow.model.Usuario;
import com.API.EventFlow.repository.CompraRepository;
import com.API.EventFlow.repository.TipoBoletoRepository;
import com.API.EventFlow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para lógica de negocio relacionada con Compra de boletos.
 *
 * <p>
 * La clase {@code CompraService} encapsula toda la lógica de negocio para operaciones
 * CRUD de compras, incluyendo validación de stock, cálculo de totales, y auditoría de transacciones.
 * Utiliza inyección de dependencias por constructor para acceder a repositorios y mappers.
 * </p>
 *
 * <p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 *   <li>Crear nuevas compras con validación de disponibilidad</li>
 *   <li>Validar cantidad de boletos disponibles</li>
 *   <li>Calcular total de compra (cantidad × precio)</li>
 *   <li>Actualizar stock de boletos automáticamente</li>
 *   <li>Gestionar actualizaciones y eliminaciones de compras</li>
 *   <li>Mapear entre DTO y entidades</li>
 *   <li>Manejar transacciones atómicas</li>
 * </ul>
 *
 * <p>
 * <strong>Validaciones de negocio:</strong>
 * </p>
 * <ul>
 *   <li>Tipo de boleto existe y es válido</li>
 *   <li>Stock disponible ≥ cantidad solicitada</li>
 *   <li>Usuario autenticado existe en BD</li>
 *   <li>Compra a actualizar existe en BD</li>
 * </ul>
 *
 * <p>
 * <strong>Transactional:</strong>
 * Los métodos de creación, actualización y eliminación están marcados con {@code @Transactional}
 * para garantizar atomicidad: o todos los cambios se aplican o ninguno.
 * </p>
 *
 * @author EventFlow Team
 * @version 1.0
 * @since 1.0
 * @see Compra
 * @see CompraDTO
 * @see CompraRepository
 * @see CompraMapper
 */
@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final TipoBoletoRepository tipoBoletoRepository;
    private final CompraMapper compraMapper;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea una nueva compra validando disponibilidad y calculando totales.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Valida que el tipo de boleto exista en BD</li>
     *   <li>Válida que hay suficientes boletos disponibles</li>
     *   <li>Obtiene el usuario autenticado desde BD</li>
     *   <li>Crea entidad Compra con datos del DTO</li>
     *   <li>Calcula total: cantidad × precio unitario</li>
     *   <li>Guarda compra en BD</li>
     *   <li>Actualiza stock del tipo de boleto</li>
     *   <li>Retorna DTO con compra creada</li>
     * </ol>
     *
     * <p>
     * <strong>Validaciones:</strong>
     * </p>
     * <ul>
     *   <li>TipoBoleto debe existir (RecursoNoEncontradoException)</li>
     *   <li>Stock disponible ≥ cantidad (StockInsuficienteException)</li>
     *   <li>Usuario debe existir (RecursoNoEncontradoException)</li>
     * </ul>
     *
     * <p>
     * <strong>Transacción atómica:</strong>
     * Si algún paso falla, toda la compra se revierte.
     * </p>
     *
     * @param compraDTO DTO con cantidad y tipoBoletoId
     * @param correo Email del usuario autenticado (desde token JWT)
     * @return CompraDTO con compra creada incluyendo ID generado
     *
     * @throws RecursoNoEncontradoException si tipo de boleto o usuario no existen
     * @throws StockInsuficienteException si no hay suficientes boletos disponibles
     *
     * @example
     * CompraDTO dto = new CompraDTO();
     * dto.setCantidad(2);
     * dto.setTipoBoletoId(5);
     * CompraDTO resultado = compraService.crearCompra(dto, "usuario@example.com");
     * // resultado.getId() = 10 (ID generado)
     * // resultado.getCompraTotal() = 299.98 (2 × 149.99)
     */
    @Transactional
    public CompraDTO crearCompra(CompraDTO compraDTO, String correo){
        // Se busca el tipo boleto en la bd.
        TipoBoleto tipoBoleto = tipoBoletoRepository.findById(compraDTO.getTipoBoletoId())
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("El tipo boleto no existe " + compraDTO.getTipoBoletoId()));

        // Se valida que la cantidad de boletos disponibles sea mayor a la cantidad comprada
        int disponible = tipoBoleto.getBoletosDisponibles();
        if (disponible < compraDTO.getCantidad()){
            throw new StockInsuficienteException
                    ("No hay boletos disponibles para el tipo boleto " + tipoBoleto.getNombreTipo());
        }

        // Buscamos el usuario que hizo la compra.
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("El usuario no existe con el correo " + correo));

        // Creamos la instancia de compra para crear la entidad compra.
        Compra compra = new Compra();
        compra.setTipoBoleto(tipoBoleto);
        compra.setFechaDeCompra(LocalDateTime.now());
        compra.setCantidad(compraDTO.getCantidad());

        // Asociamos el usuario a la compra.
        compra.setCliente(usuario);

        // Calcular el total de la compra.
        BigDecimal precioUnitario = tipoBoleto.getPrecio();
        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(compraDTO.getCantidad()));
        compra.setCompraTotal(total);

        // Guardamos la compra en la bd.
        Compra compraCreada = compraRepository.save(compra);

        // Actualizamos el stock.
        Integer nuevoStock = tipoBoleto.getBoletosDisponibles() - compraCreada.getCantidad();
        tipoBoleto.setBoletosDisponibles(nuevoStock);

        // Guardar el nuevo estado del tipo boleto.
        tipoBoletoRepository.save(tipoBoleto);

        // Retornar el DTO con la compra actualizada.
        return compraMapper.convertirEntidadaDTO(compraCreada);
    }

    /**
     * Obtiene una compra específica por ID.
     *
     * @param id ID de la compra a buscar
     * @return CompraDTO con datos de la compra
     * @throws RecursoNoEncontradoException si la compra no existe
     *
     * @example
     * CompraDTO compra = compraService.listarCompraPorId(10L);
     * System.out.println("Cantidad: " + compra.getCantidad());
     */
    public CompraDTO listarCompraPorId(Long id){
        Compra compraEncontrada = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no se encuentra con ID " + id));
        return compraMapper.convertirEntidadaDTO(compraEncontrada);
    }

    /**
     * Obtiene todas las compras registradas en el sistema.
     *
     * <p>
     * <strong>Nota:</strong> Este método lanza excepción si no hay compras.
     * En producción, considerar devolver lista vacía en lugar de excepción.
     * </p>
     *
     * @return Lista de CompraDTO de todas las compras
     * @throws RecursoNoEncontradoException si no hay compras en el sistema
     *
     * @example
     * List<CompraDTO> compras = compraService.listarTodosLasCompras();
     * System.out.println("Total de compras: " + compras.size());
     */
    public List<CompraDTO> listarTodosLasCompras(){
        List<Compra> compras = compraRepository.findAll();
        if (compras.isEmpty()){
            throw new RecursoNoEncontradoException("La lista de compra esta vacía");
        }
        return compras.stream().map(compraMapper::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una compra existente, incluyendo ajustes de stock.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Valida que compra a actualizar existe</li>
     *   <li>Obtiene nuevo tipo de boleto de BD</li>
     *   <li>Revierte stock del tipo de boleto anterior</li>
     *   <li>Actualiza campos de la compra</li>
     *   <li>Válida disponibilidad del nuevo tipo de boleto</li>
     *   <li>Descuenta nuevo stock</li>
     *   <li>Recalcula total con nuevo precio</li>
     *   <li>Guarda cambios en BD</li>
     * </ol>
     *
     * <p>
     * <strong>Gestión de stock compleja:</strong>
     * Si se cambia tipo de boleto, debe revertir el anterior y descontar el nuevo.
     * </p>
     *
     * @param compraDTO DTO con nuevos datos de compra
     * @param id ID de compra a actualizar
     * @param correo Email del usuario autenticado
     * @return CompraDTO con compra actualizada
     *
     * @throws RecursoNoEncontradoException si compra, tipo o usuario no existen
     * @throws StockInsuficienteException si no hay stock del nuevo tipo
     *
     * @example
     * CompraDTO dto = new CompraDTO();
     * dto.setCantidad(3);
     * dto.setTipoBoletoId(6);
     *
     * CompraDTO actualizada = compraService.actualizarCompra(dto, 10L, "usuario@example.com");
     */
    @Transactional
    public CompraDTO actualizarCompra(CompraDTO compraDTO, Long id, String correo){

        // Buscamos la compra existente en la base de datos
        Compra compraExistente = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("La compra no existe con el ID " + id));

        // Buscamos el boleto existente en la base de datos.
        TipoBoleto tipoBoletoNuevo = tipoBoletoRepository.findById(compraDTO.getTipoBoletoId())
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("El boleto no existe en la db de datos con el id" + compraDTO.getTipoBoletoId()));

        // Buscamos el usuario que hizo la compra.
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException
                        ("El usuario no existe con el correo " + correo));


        // Revertir el stock anterior
        TipoBoleto tipoBoletoAnterior = compraExistente.getTipoBoleto();
        tipoBoletoAnterior.setBoletosDisponibles(
                tipoBoletoAnterior.getBoletosDisponibles() + compraExistente.getCantidad());

        // Actualizamos los campos de la nueva compra.
        compraExistente.setFechaDeCompra(LocalDateTime.now());
        compraExistente.setCantidad(compraDTO.getCantidad());
        compraExistente.setTipoBoleto(tipoBoletoNuevo);
        compraExistente.setCliente(usuario);


        // Validamos la cantidad de boletos disponibles antes de descontar la nueva cantidad de la venta.
        if (tipoBoletoNuevo.getBoletosDisponibles() < compraDTO.getCantidad()) {
            throw new StockInsuficienteException("La cantidad de boletos existentes es menor ala cantidad comprada" + tipoBoletoNuevo.getBoletosDisponibles());
        }

        tipoBoletoNuevo.setBoletosDisponibles(tipoBoletoNuevo.getBoletosDisponibles() - compraDTO.getCantidad());
        compraExistente.setCompraTotal(tipoBoletoNuevo.getPrecio().multiply(BigDecimal.valueOf(compraDTO.getCantidad())));


        // Guardamos la compra actualizada en la bd.
        tipoBoletoRepository.save(tipoBoletoAnterior);
        tipoBoletoRepository.save(tipoBoletoNuevo);
        Compra compraActualizada = compraRepository.save(compraExistente);
        return compraMapper.convertirEntidadaDTO(compraActualizada);
    }

    /**
     * Elimina una compra del sistema y revierte su stock.
     *
     * <p>
     * <strong>Nota importante:</strong> El stock NO se revierte automáticamente.
     * Considerar agregar lógica para devolver boletos al stock.
     * </p>
     *
     * @param id ID de compra a eliminar
     * @throws RecursoNoEncontradoException si la compra no existe
     *
     * @example
     * compraService.eliminarCompraId(10L);
     * System.out.println("Compra eliminada exitosamente");
     */
    @Transactional
    public void eliminarCompraId(Long id){
        Compra compraEncontrada = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no se encuentra con el ID " + id));
        compraRepository.delete(compraEncontrada);
    }
}
