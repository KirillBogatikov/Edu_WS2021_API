package org.ws2021.web;

import java.util.UUID;

import org.ws2021.models.auth.Token;
import org.ws2021.util.DateUtil;
import org.ws2021.util.JwtAuth;

public class EnhancedController {
    protected JwtAuth jwt;
    
    public EnhancedController(JwtAuth jwt) {
        this.jwt = jwt;
    }
    
    public UUID auth(String authHeader) {
        Token token = jwt.parse(authHeader.split(" ")[1]);
        
        if (token.getExpiration().after(DateUtil.now())) {
            return token.getUserId();
        }
        
        return null;
    }
}
