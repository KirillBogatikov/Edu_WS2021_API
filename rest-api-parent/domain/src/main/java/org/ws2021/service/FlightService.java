package org.ws2021.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.ws2021.data.models.Flight;
import org.ws2021.maintain.HealthMonitor;
import org.ws2021.repository.FlightRepository;

public class FlightService {
    private HealthMonitor health;
    private FlightRepository flights;
    
    public FlightService(HealthMonitor health, FlightRepository flights) {
        this.health = health;
        this.flights = flights;
    }
    
    public List<Flight> getAll(String fromIata, String toIata, Date periodStart, Date periodEnd, int passenger) {
        try {
            return flights.list(fromIata, toIata, periodStart, periodEnd, passenger);
        } catch(SQLException e) {
            health.repositoryError("FlightRepository", "getAll", e);
            return null;
        }
    }
    
    public Flight getById(int id) {
        try {
            return flights.byId(id);
        } catch (SQLException e) {
            health.repositoryError("FlightRepository", "byId", e);
            return null;
        }
    }
}
