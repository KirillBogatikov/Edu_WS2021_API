package org.ws2021.repository;

import static org.ws2021.util.Extractor.readTextSilent;
import static org.ws2021.util.SqlResult.readUUID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.ws2021.data.models.Flight;
import org.ws2021.data.models.Place;
import org.ws2021.sql.Database;
import org.ws2021.sql.ModelMapper;
import org.ws2021.util.DateUtil;
import org.ws2021.util.Mapping;

public class FlightRepository {
    private static final String LIST = readTextSilent("flight/list.sql");
    private static final String BY_ID = readTextSilent("flight/by_id.sql");
    public static final ModelMapper<Flight> MAPPER = (r) -> {
        Timestamp ts = r.getTimestamp("fromDateTime");
        Place from = new Place(readUUID(r, "fromId"), r.getString("fromCity"), r.getString("fromAirport"),
                r.getString("fromIata"), new Date(ts.getTime()));
        
        ts = r.getTimestamp("toDateTime");
        Place to = new Place(readUUID(r, "toId"), r.getString("toCity"), r.getString("toAirport"),
                r.getString("toIata"), new Date(ts.getTime()));

        return new Flight(r.getInt("flightId"), r.getString("code"), from, to, r.getDouble("cost"), r.getInt("availability"));
    };

    private Database database;

    public FlightRepository(Database database) {
        this.database = database;
    }

    public Flight byId(int id) throws SQLException {
        return database.prepareStatement(BY_ID, (c, s) -> {
            s.setInt(1, id);
            ResultSet r = s.executeQuery();
            if (r.next()) {
                return MAPPER.map(r);
            }
            return null;
        });
    }

    public List<Flight> list(String from, String to, Date periodStart, Date periodEnd, int passengers) throws SQLException {
        return database.prepareStatement(LIST, (c, s) -> {            
            s.setString(1, from);
            s.setString(2, to);
            s.setTimestamp(3, DateUtil.sqlTimestamp(periodStart));
            s.setTimestamp(4, DateUtil.sqlTimestamp(periodEnd));
            s.setInt(5, passengers);
            
            return Mapping.listOf(MAPPER).map(s.executeQuery());
        });
    }
}
