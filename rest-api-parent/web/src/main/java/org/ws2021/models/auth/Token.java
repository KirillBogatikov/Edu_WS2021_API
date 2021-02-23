package org.ws2021.models.auth;

import java.util.Date;
import java.util.UUID;

public class Token {
    private UUID userId;
    private Date expiration;
    
    public Token(UUID userId, Date expiration) {
        this.userId = userId;
        this.expiration = expiration;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
