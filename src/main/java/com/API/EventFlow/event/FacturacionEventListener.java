package com.API.EventFlow.event;

import com.API.EventFlow.model.Compra;
import com.API.EventFlow.repository.CompraRepository;
import com.API.EventFlow.service.FacturacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class FacturacionEventListener {

    private final FacturacionService facturacionService;
    private final CompraRepository compraRepository;

    /**
     * Escucha el evento CompraRealizadaEvent y ejecuta la lógica de facturación
     * DESPUÉS de que la transacción de la compra se haya completado (AFTER_COMMIT).
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCompraRealizadaEvent(CompraRealizadaEvent event) {
        log.info("Evento CompraRealizadaEvent recibido para la compra ID: {}", event.getCompraId());
        
        // Buscamos la compra en la base de datos, que ahora garantizamos que existe.
        Compra compra = compraRepository.findById(event.getCompraId())
                .orElse(null);

        if (compra != null) {
            facturacionService.generarYGuardarFactura(compra);
        } else {
            log.error("No se pudo encontrar la compra con ID {} para generar la factura.", event.getCompraId());
        }
    }
}
