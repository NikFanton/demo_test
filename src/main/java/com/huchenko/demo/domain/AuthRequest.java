package com.huchenko.demo.domain;

public class AuthRequest {
    private String apiKey;

    public AuthRequest(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
