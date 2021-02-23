package org.ws2021.service;

import java.sql.SQLException;

import org.ws2021.data.models.Booking;
import org.ws2021.data.models.BookingStatus;
import org.ws2021.maintain.HealthMonitor;
import org.ws2021.repository.BookingRepository;

public class BookService {
    private HealthMonitor health;
    private BookingRepository booking;
    
    public BookService(HealthMonitor health, BookingRepository booking) {
        this.health = health;
        this.booking = booking;
    }
    
    public Booking getByCode(String code) {
        try {
            return booking.byId(booking.idByCode(code));
        } catch (SQLException e) {
            health.repositoryError("BookingRepository", "save", e);
            return null;
        }
    }

    public BookingStatus book(Booking booking) {
        try {
            return this.booking.save(booking);
        } catch (SQLException e) {
            health.repositoryError("BookingRepository", "save", e);
            return null;
        }
    }
    
    public BookingStatus setSeatFrom(String code, int passenger, String seat) {
        try {
            return booking.setSeat(code, passenger, seat, null);
        } catch (SQLException e) {
            health.repositoryError("BookingRepository", "setSeatFrom", e);
            return null;
        }
    }
    
    public BookingStatus setSeatBack(String code, int passenger, String seat) {
        try {
            return booking.setSeat(code, passenger, null, seat);
        } catch (SQLException e) {
            health.repositoryError("BookingRepository", "setSeatTo", e);
            return null;
        }
    }
}
