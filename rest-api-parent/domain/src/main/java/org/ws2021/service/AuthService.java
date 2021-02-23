package org.ws2021.service;

import java.sql.SQLException;
import java.util.Arrays;

import org.ws2021.data.models.User;
import org.ws2021.maintain.HealthMonitor;
import org.ws2021.repository.UserRepository;
import org.ws2021.util.Sha256;

public class AuthService {
    private HealthMonitor health;
    private UserRepository users;
    
    public AuthService(HealthMonitor health, UserRepository users) {
        this.health = health;
        this.users = users;
    }

    public Object[] signIn(String phone, String password) {
        try {
            byte[] expectedHash = users.hashByPhone(phone);
            boolean passwordCorrect = Arrays.equals(expectedHash, Sha256.hashOf(password.getBytes()));
            if (passwordCorrect) {
                return new Object[] { users.idByPhone(phone), true };
            }
            
            return new Object[] { null, false };
        } catch(SQLException e) {
            health.repositoryError("UserRepository", "hashByPhone", e);
            return null;
        }
    }
    
    public boolean signUp(User user) {
        try {
            users.add(user);
            return true;
        } catch(SQLException e) {
            health.repositoryError("UserRepository", "add", e);
            return false;
        }
    }
}
