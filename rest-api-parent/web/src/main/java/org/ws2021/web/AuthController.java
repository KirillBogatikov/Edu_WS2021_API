package org.ws2021.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.ws2021.data.models.User;
import org.ws2021.models.WebUser;
import org.ws2021.models.auth.Credentials;
import org.ws2021.models.auth.TokenHolder;
import org.ws2021.models.holder.DataHolder;
import org.ws2021.models.holder.ErrorHolder;
import org.ws2021.models.holder.Holder;
import org.ws2021.service.AuthService;
import org.ws2021.util.JwtAuth;
import org.ws2021.util.Sha256;

@RestController
public class AuthController {
    private AuthService auth;
    private JwtAuth jwt;
    
    public AuthController(AuthService auth, JwtAuth jwt) {
        this.auth = auth;
        this.jwt = jwt;
    }

    @PostMapping("/api/login")
    public ResponseEntity<Holder> signIn(@RequestBody Credentials credentials) {
        String phone = credentials.getPhone();
        String password = credentials.getPassword();
        
        ErrorHolder errorHolder = new ErrorHolder(422, "Validation error");
        validateCredentials(phone, password, errorHolder.getError().getErrors());
        
        if (!errorHolder.isEmpty()) {
            return new ResponseEntity<>(errorHolder, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        Object[] result = auth.signIn(phone, password);
        if (result != null) {
            if (result[0] != null) {
                UUID id = (UUID)result[0];
                DataHolder<TokenHolder> holder = new DataHolder<>(new TokenHolder(jwt.generate(id)));
                return new ResponseEntity<>(holder, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
            
        errorHolder = new ErrorHolder(401, "Unauthorized");
        errorHolder.addError("phone", "phone or password incorrect");
        
        return new ResponseEntity<>(errorHolder, HttpStatus.UNAUTHORIZED);
    }
    
    @PostMapping("/api/register")
    public ResponseEntity<Holder> signUp(@RequestBody WebUser webUser) {
        Map<String, String[]> errors = validateUser(webUser);
        if (!errors.isEmpty()) {
            ErrorHolder holder = new ErrorHolder(422, "Validation error");
            holder.getError().setErrors(errors);
            return new ResponseEntity<>(holder, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        byte[] hash = Sha256.hashOf(webUser.getPassword().getBytes());
        User user = new User(UUID.randomUUID(), hash, webUser.getFirstName(), webUser.getLastName(), webUser.getPhone(), webUser.getDocumentNumber());
        Boolean ok = auth.signUp(user);
        
        if (ok) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private void validateCredentials(String phone, String password, Map<String, String[]> errors) {
        if (password == null || !password.matches("(\\w|\\d){4,32}")) {
            errors.put("password", new String[] { "invalid or empty", "should from 4 to 32 letters A-Z or numbers 0-9", "example: abc123d4" });
        }
        if (phone == null || !phone.matches("(\\+7|8)(\\d){10}")) {
            errors.put("phone", new String[] { "invalid or empty" });
        }
    }
    
    private Map<String, String[]> validateUser(WebUser user) {
        Map<String, String[]> errors = new HashMap<>();
        
        if (user.getFirstName() == null || !user.getFirstName().matches("[a-zA-Zа-яА-Я]+")) {
            errors.put("first_name", new String[]{ "first name required" });
        }
        if (user.getLastName() == null || !user.getLastName().matches("[a-zA-Zа-яА-Я]+")) {
            errors.put("last_name", new String[] { "last name required" });
        }
        validateCredentials(user.getPhone(), user.getPassword(), errors);
        
        return errors;
    }
}
