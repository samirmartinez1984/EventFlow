package com.API.EventFlow.service;

import com.API.EventFlow.factus.dto.FacturaRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class FactusService {

    private final FactusAuthService factusAuthService;
    private final RestTemplate restTemplate;

    @Value("${factus.url}")
    private String baseUrl;

    public String crearFactura(FacturaRequestDTO facturaRequest) {
        String token = factusAuthService.obtenerToken();
        String url = baseUrl + "/v1/bills/validate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);

        HttpEntity<FacturaRequestDTO> request = new HttpEntity<>(facturaRequest, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);

        // MODO DE DIAGNÓSTICO FINAL: Si la respuesta no es exitosa, lanzar un error.
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error de Factus: " + response.getStatusCode() + " " + response.getBody());
        }

        JsonNode body = response.getBody();
        if (body != null) {
            JsonNode qrNode = body.path("data").path("bill").path("qr");
            if (!qrNode.isMissingNode()) {
                return qrNode.asText();
            }
        }

        // Si no se encuentra el QR, lanzar un error para saberlo.
        throw new RuntimeException("La respuesta de Factus fue exitosa, pero no se encontró el campo 'qr'. Respuesta: " + body);
    }
}
