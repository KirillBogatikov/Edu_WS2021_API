package org.ws2021.service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.ws2021.data.models.Booking;
import org.ws2021.data.models.User;
import org.ws2021.maintain.HealthMonitor;
import org.ws2021.repository.BookingRepository;
import org.ws2021.repository.UserRepository;

public class UserService {
    private HealthMonitor health;
    private UserRepository users;
    private BookingRepository booking;
    
    public UserService(HealthMonitor health, UserRepository users, BookingRepository booking) {
        this.health = health;
        this.users = users;
        this.booking = booking;
    }

    public User get(UUID id) {
        try {
            return users.byId(id);
        } catch(SQLException e) {
            health.repositoryError("UserRepository", "byId", e);
            return null;
        }
    }
    
    public List<Booking> listBooking(UUID id) {
        try {
            return booking.list(id);
        } catch(SQLException e) {
            health.repositoryError("BookingRepository", "list", e);
            return null;
        }
    }
}
