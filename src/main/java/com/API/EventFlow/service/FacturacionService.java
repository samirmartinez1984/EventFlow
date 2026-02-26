package com.API.EventFlow.service;

import com.API.EventFlow.factus.dto.FacturaItemDTO;
import com.API.EventFlow.factus.dto.FacturaRequestDTO;
import com.API.EventFlow.factus.dto.FactusCustomerDTO;
import com.API.EventFlow.model.Compra;
import com.API.EventFlow.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturacionService {

    private final FactusService factusService;
    private final CompraRepository compraRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generarYGuardarFactura(Compra compra) {
        try {
            FactusCustomerDTO customerDTO = new FactusCustomerDTO(
                    3, // Cédula de ciudadanía
                    compra.getCliente().getCedula(),
                    compra.getCliente().getNombre() + " " + compra.getCliente().getPrimerApellido(),
                    compra.getCliente().getCorreo(),
                    "2", // Persona Natural
                    "21", // No aplica
                    980 // Municipio de ejemplo
            );

            FacturaItemDTO itemDTO = new FacturaItemDTO(
                    "BOLETO-" + compra.getTipoBoleto().getId(),
                    compra.getTipoBoleto().getNombreTipo(),
                    compra.getCantidad(),
                    0.0f,
                    compra.getTipoBoleto().getPrecio(),
                    "0.00",
                    70,
                    1,
                    1,
                    1
            );

            // SOLUCIÓN: Generar un código de referencia garantizado único usando un timestamp.
            String referenceCode = "FACT-" + System.currentTimeMillis();

            FacturaRequestDTO facturaRequest = new FacturaRequestDTO(
                    8,
                    referenceCode,
                    customerDTO,
                    Collections.singletonList(itemDTO)
            );

            String facturaUrl = factusService.crearFactura(facturaRequest);

            if (facturaUrl != null) {
                Compra compraParaActualizar = compraRepository.findById(compra.getId()).orElseThrow();
                compraParaActualizar.setFacturaUrl(facturaUrl);
                compraRepository.save(compraParaActualizar);
                log.info("Factura generada y guardada para la compra ID: {}", compra.getId());
            } else {
                log.warn("No se recibió URL de factura para la compra ID: {}", compra.getId());
            }

        } catch (Exception e) {
            log.error("Error al generar la factura para la compra ID: {}. La compra se completó igualmente.", compra.getId(), e);
        }
    }
}
