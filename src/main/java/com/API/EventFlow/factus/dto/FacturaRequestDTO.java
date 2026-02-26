package com.API.EventFlow.factus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO principal y detallado para la solicitud de factura.
 */
public record FacturaRequestDTO(
    @JsonProperty("numbering_range_id")
    Integer numberingRangeId,

    @JsonProperty("reference_code")
    String referenceCode,

    @JsonProperty("customer")
    FactusCustomerDTO customer,

    @JsonProperty("items")
    List<FacturaItemDTO> items
) {}
