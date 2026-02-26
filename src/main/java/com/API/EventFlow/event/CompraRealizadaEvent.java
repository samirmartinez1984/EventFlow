package com.API.EventFlow.event;

import org.springframework.context.ApplicationEvent;

/**
 * Evento que se publica cuando una compra se ha completado y confirmado.
 */
public class CompraRealizadaEvent extends ApplicationEvent {

    private final Long compraId;

    public CompraRealizadaEvent(Object source, Long compraId) {
        super(source);
        this.compraId = compraId;
    }

    public Long getCompraId() {
        return compraId;
    }
}
