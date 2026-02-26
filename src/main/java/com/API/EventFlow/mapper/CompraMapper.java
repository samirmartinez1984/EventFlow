package com.API.EventFlow.mapper;

import com.API.EventFlow.dto.CompraDTO;
import com.API.EventFlow.model.Compra;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper para convertir entre Compra (entidad JPA) y CompraDTO.
 */
@Component
public class CompraMapper {

    /**
     * Convierte un CompraDTO a una entidad Compra.
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
     */
    public CompraDTO convertirEntidadaDTO(Compra compra){
        CompraDTO compraDTO = new CompraDTO();
        compraDTO.setId(compra.getId());
        compraDTO.setFechaDeCompra(compra.getFechaDeCompra());
        compraDTO.setCantidad(compra.getCantidad());
        compraDTO.setTipoBoletoId(compra.getTipoBoleto().getId());
        compraDTO.setCompraTotal(compra.getCompraTotal());
        compraDTO.setFacturaUrl(compra.getFacturaUrl());
        return compraDTO;
    }
}
