package org.ws2021.service;

import java.sql.SQLException;
import java.util.List;

import org.ws2021.data.models.Airport;
import org.ws2021.maintain.HealthMonitor;
import org.ws2021.repository.AirportRepository;

public class AirportService {
    private HealthMonitor health;
    private AirportRepository airports;
        
    public AirportService(HealthMonitor health, AirportRepository airports) {
        this.health = health;
        this.airports = airports;
    }

    public List<Airport> list(String query) {
        try {
            return airports.listAll(String.format("[A-Z]*%s[A-Z]*", query));
        } catch (SQLException e) {
            health.repositoryError("AirportRepository", "listAll", e);
            return null;
        }
    }
}
