package com.API.EventFlow.factus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * DTO detallado para cada ítem, basado en la documentación de la API.
 */
public record FacturaItemDTO(
    @JsonProperty("code_reference")
    String codeReference,

    @JsonProperty("name")
    String name,

    @JsonProperty("quantity")
    Integer quantity,

    @JsonProperty("discount_rate")
    Float discountRate,

    @JsonProperty("price")
    BigDecimal price,

    @JsonProperty("tax_rate")
    String taxRate,

    @JsonProperty("unit_measure_id")
    Integer unitMeasureId,

    @JsonProperty("standard_code_id")
    Integer standardCodeId,

    @JsonProperty("is_excluded")
    Integer isExcluded,

    @JsonProperty("tribute_id")
    Integer tributeId
) {}
