package com.API.EventFlow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cantidad_comprada")
    private Integer cantidad;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaDeCompra;

    @Column(name = "compra_total")
    private BigDecimal compraTotal;

    @Column(name = "factura_url", length = 512)
    private String facturaUrl;

    @ManyToOne
    @JoinColumn(name = "tipo_boleto_id")
    private TipoBoleto tipoBoleto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente;
}
