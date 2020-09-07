package com.huchenko.demo.service;

import com.huchenko.demo.domain.AuthRequest;
import com.huchenko.demo.domain.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthorizationService {

    private final RestTemplate restTemplate;

    @Value("${application.picturesEndpoint.url}")
    private String picturesEndpoint;

    @Value("${application.picturesEndpoint.key}")
    private String apiKey;

    public AuthorizationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getToken() {
        String authEndpoint = picturesEndpoint + "auth";
        HttpEntity<?> httpEntity = new HttpEntity<>(new AuthRequest(apiKey), HttpHeaders.EMPTY);
        AuthResponse response
            = restTemplate.exchange(authEndpoint, HttpMethod.POST, httpEntity, AuthResponse.class).getBody();
        if (response.isAuth()) {
            return response.getToken();
        } else {
            throw new RuntimeException("Auth Failed");
        }

    }
}
