package com.API.EventFlow.factus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO inmutable (record) para representar los datos del cliente (tercero).
 */
public record FacturaTerceroDTO(
    @JsonProperty("first_name")
    String nombres,

    @JsonProperty("last_name")
    String apellidos,

    @JsonProperty("email")
    String correo,

    @JsonProperty("identification_number")
    String cedula
) {}
