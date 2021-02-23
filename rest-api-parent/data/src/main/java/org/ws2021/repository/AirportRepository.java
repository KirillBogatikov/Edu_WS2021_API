package org.ws2021.repository;

import static org.ws2021.util.Extractor.readTextSilent;

import java.sql.SQLException;
import java.util.List;

import org.ws2021.data.models.Airport;
import org.ws2021.sql.Database;
import org.ws2021.sql.ModelMapper;
import org.ws2021.util.Mapping;

public class AirportRepository {
    private static final String INSERT = readTextSilent("airport/insert.sql");
    private static final String UPDATE = readTextSilent("airport/update.sql");
    private static final String DELETE = readTextSilent("airport/delete.sql");
    private static final String LIST_ALL = readTextSilent("airport/list_all.sql");
    private static final ModelMapper<Airport> MAPPER = (r) -> new Airport(r.getString("airportIata"), r.getString("airportName"));

    private Database database;

    public AirportRepository(Database database) {
        this.database = database;
    }

    public List<Airport> listAll(String query) throws SQLException {
        return database.prepareStatement(LIST_ALL, (c, s) -> {
            s.setString(1, query);
            s.setString(2, query);
            
            return Mapping.listOf(MAPPER).map(s.executeQuery());
        });
    }

    public boolean add(Airport airport) throws SQLException {
        return database.prepareStatement(INSERT, (c, s) -> {
            s.setString(1, airport.getIata());
            s.setString(2, airport.getName());
            return s.execute();
        });
    }

    public boolean update(Airport airport) throws SQLException {
        return database.prepareStatement(UPDATE, (c, s) -> {
            s.setString(1, airport.getName());
            s.setString(2, airport.getIata());
            return s.execute();
        });
    }

    public boolean delete(Airport airport) throws SQLException {
        return database.prepareStatement(DELETE, (c, s) -> {
            s.setString(1, airport.getIata());
            return s.execute();
        });
    }
}
