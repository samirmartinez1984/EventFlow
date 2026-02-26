package com.API.EventFlow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FactusAuthService {

    @Value("${factus.url}")
    private String baseUrl;

    @Value("${factus.client-id}")
    private String clientId;

    @Value("${factus.client-secret}")
    private String clientSecret;

    @Value("${factus.username}")
    private String username;

    @Value("${factus.password}")
    private String password;

    // Se inyecta el RestTemplate configurado centralmente
    private final RestTemplate restTemplate;

    public String obtenerToken() {
        String url = baseUrl + "/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }
        
        return null;
    }
}
