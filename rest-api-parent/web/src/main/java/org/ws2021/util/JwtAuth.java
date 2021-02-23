package org.ws2021.util;

import java.util.UUID;

import javax.crypto.SecretKey;

import org.ws2021.models.auth.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class JwtAuth {    
    private SecretKey secretKey;
    
    public JwtAuth(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
    
    public String generate(UUID userId) {
        return Jwts.builder()
            .setId(userId.toString())
            .setExpiration(DateUtil.nowPlusHours(24))
            .signWith(secretKey)
            .compact();
    }
    
    public Token parse(String tokenBase64) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(tokenBase64);       
        Claims claims = jws.getBody();
        return new Token(UUID.fromString(claims.getId()), claims.getExpiration()); 
    }
}
