package org.ws2021.models.auth;

public class TokenHolder {
    private String token;

    public TokenHolder(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
