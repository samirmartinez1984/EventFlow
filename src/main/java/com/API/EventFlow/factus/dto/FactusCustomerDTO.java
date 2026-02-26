package com.API.EventFlow.factus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO detallado para el cliente (customer), basado en la documentación de la API.
 */
public record FactusCustomerDTO(
    @JsonProperty("identification_document_id")
    Integer identificationDocumentId,

    @JsonProperty("identification")
    String identification,

    @JsonProperty("names")
    String names,

    @JsonProperty("email")
    String email,

    @JsonProperty("legal_organization_id")
    String legalOrganizationId,

    @JsonProperty("tribute_id")
    String tributeId,
    
    @JsonProperty("municipality_id")
    Integer municipalityId
) {}
