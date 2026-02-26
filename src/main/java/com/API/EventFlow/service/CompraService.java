package com.API.EventFlow.service;

import com.API.EventFlow.dto.CompraDTO;
import com.API.EventFlow.event.CompraRealizadaEvent;
import com.API.EventFlow.exceptiones.RecursoNoEncontradoException;
import com.API.EventFlow.exceptiones.StockInsuficienteException;
import com.API.EventFlow.mapper.CompraMapper;
import com.API.EventFlow.model.Compra;
import com.API.EventFlow.model.TipoBoleto;
import com.API.EventFlow.model.Usuario;
import com.API.EventFlow.repository.CompraRepository;
import com.API.EventFlow.repository.TipoBoletoRepository;
import com.API.EventFlow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final TipoBoletoRepository tipoBoletoRepository;
    private final CompraMapper compraMapper;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CompraDTO crearCompra(CompraDTO compraDTO, String correo){
        TipoBoleto tipoBoleto = tipoBoletoRepository.findById(compraDTO.getTipoBoletoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El tipo boleto no existe " + compraDTO.getTipoBoletoId()));

        int disponible = tipoBoleto.getBoletosDisponibles();
        if (disponible < compraDTO.getCantidad()){
            throw new StockInsuficienteException("No hay boletos disponibles para el tipo boleto " + tipoBoleto.getNombreTipo());
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe con el correo " + correo));

        Compra compra = new Compra();
        compra.setTipoBoleto(tipoBoleto);
        compra.setFechaDeCompra(LocalDateTime.now());
        compra.setCantidad(compraDTO.getCantidad());
        compra.setCliente(usuario);

        BigDecimal precioUnitario = tipoBoleto.getPrecio();
        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(compraDTO.getCantidad()));
        compra.setCompraTotal(total);

        Compra compraCreada = compraRepository.save(compra);

        Integer nuevoStock = tipoBoleto.getBoletosDisponibles() - compraCreada.getCantidad();
        tipoBoleto.setBoletosDisponibles(nuevoStock);
        tipoBoletoRepository.save(tipoBoleto);

        eventPublisher.publishEvent(new CompraRealizadaEvent(this, compraCreada.getId()));

        return compraMapper.convertirEntidadaDTO(compraCreada);
    }

    public CompraDTO listarCompraPorId(Long id){
        Compra compraEncontrada = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no se encuentra con ID " + id));
        return compraMapper.convertirEntidadaDTO(compraEncontrada);
    }

    public List<CompraDTO> listarTodosLasCompras(){
        List<Compra> compras = compraRepository.findAll();
        if (compras.isEmpty()){
            throw new RecursoNoEncontradoException("La lista de compra esta vacía");
        }
        return compras.stream().map(compraMapper::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CompraDTO actualizarCompra(CompraDTO compraDTO, Long id, String correo){
        Compra compraExistente = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no existe con el ID " + id));

        TipoBoleto tipoBoletoNuevo = tipoBoletoRepository.findById(compraDTO.getTipoBoletoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El boleto no existe en la db de datos con el id" + compraDTO.getTipoBoletoId()));

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe con el correo " + correo));

        TipoBoleto tipoBoletoAnterior = compraExistente.getTipoBoleto();
        tipoBoletoAnterior.setBoletosDisponibles(
                tipoBoletoAnterior.getBoletosDisponibles() + compraExistente.getCantidad());

        compraExistente.setFechaDeCompra(LocalDateTime.now());
        compraExistente.setCantidad(compraDTO.getCantidad());
        compraExistente.setTipoBoleto(tipoBoletoNuevo);
        compraExistente.setCliente(usuario);

        if (tipoBoletoNuevo.getBoletosDisponibles() < compraDTO.getCantidad()) {
            throw new StockInsuficienteException("La cantidad de boletos existentes es menor ala cantidad comprada" + tipoBoletoNuevo.getBoletosDisponibles());
        }

        tipoBoletoNuevo.setBoletosDisponibles(tipoBoletoNuevo.getBoletosDisponibles() - compraDTO.getCantidad());
        compraExistente.setCompraTotal(tipoBoletoNuevo.getPrecio().multiply(BigDecimal.valueOf(compraDTO.getCantidad())));

        tipoBoletoRepository.save(tipoBoletoAnterior);
        tipoBoletoRepository.save(tipoBoletoNuevo);
        Compra compraActualizada = compraRepository.save(compraExistente);
        return compraMapper.convertirEntidadaDTO(compraActualizada);
    }

    @Transactional
    public void eliminarCompraId(Long id){
        Compra compraEncontrada = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no se encuentra con el ID " + id));
        compraRepository.delete(compraEncontrada);
    }
}
