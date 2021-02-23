package org.ws2021.repository;

import static org.ws2021.util.Extractor.readTextSilent;
import static org.ws2021.util.SqlResult.readUUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ws2021.data.models.Booking;
import org.ws2021.data.models.BookingStatus;
import org.ws2021.data.models.Flight;
import org.ws2021.data.models.Passenger;
import org.ws2021.sql.Database;
import org.ws2021.sql.ModelMapper;
import org.ws2021.util.DateUtil;
import org.ws2021.util.Mapping;

public class BookingRepository {
    private static final String INSERT = readTextSilent("booking/insert.sql");
    private static final String INSERT_PASSENGER = readTextSilent("booking/passenger/insert.sql");
    private static final String INSERT_FLIGHT = readTextSilent("booking/flight/insert.sql");

    private static final String SELECT = readTextSilent("booking/select.sql");
    private static final String SELECT_PASSENGER = readTextSilent("booking/passenger/select.sql");
    private static final String SELECT_FLIGHT = readTextSilent("booking/flight/select.sql");

    private static final String ID_BY_CODE = readTextSilent("booking/id_by_code.sql");
    
    private static final String UPDATE_SEAT_FROM = readTextSilent("booking/seat/update_from.sql");
    private static final String UPDATE_SEAT_BACK = readTextSilent("booking/seat/update_back.sql");
    private static final String CHECK_SEAT_FROM = readTextSilent("booking/seat/check_from.sql");
    private static final String CHECK_SEAT_BACK = readTextSilent("booking/seat/check_back.sql");
    private static final String CHECK_FLIGHT_AVAILABLE = readTextSilent("booking/flight/check_available.sql");

    private static final String GET_SEAT_ID = readTextSilent("booking/seat/id_by_passenger.sql");
    
    private static final String LIST_ALL = readTextSilent("booking/list_all.sql");

    private static final ModelMapper<Passenger> PASSENGER_MAPPER = (r) -> {
        Passenger passenger = new Passenger(readUUID(r, "id"), r.getInt("numberId"), r.getString("firstName"), r.getString("lastName"),
                new Date(r.getDate("birthDate").getTime()), r.getString("documentNumber"));

        passenger.setPlaceBack(r.getString("placeBack"));
        passenger.setPlaceFrom(r.getString("placeFrom"));

        return passenger;
    };

    private Database database;

    public BookingRepository(Database database) {
        this.database = database;
    }

    public BookingStatus save(Booking booking) throws SQLException {
        try(Connection connection = database.connect(false)) {
            PreparedStatement insertMain = connection.prepareStatement(INSERT);
            insertMain.setObject(1, booking.getId());
            insertMain.setString(2, booking.getCode());

            insertMain.setObject(3, UUID.randomUUID());
            insertMain.setObject(4, booking.getUserId());
            insertMain.setObject(5, booking.getId());

            insertMain.execute();
            insertMain.close();

            List<Passenger> passengers = booking.getPassengers();
            for (int i = 0; i < passengers.size(); i++) {
                Passenger pax = passengers.get(i);
                
                PreparedStatement insertPax = connection.prepareStatement(INSERT_PASSENGER);
                
                insertPax.setObject(1, pax.getId());
                insertPax.setInt(2, pax.getNumberId());
                insertPax.setString(3, pax.getFirstName());
                insertPax.setString(4, pax.getLastName());
                insertPax.setDate(5, DateUtil.sqlDate(pax.getBirthDate()));
                insertPax.setString(6, pax.getDocumentNumber());

                insertPax.setObject(7, UUID.randomUUID());
                insertPax.setObject(8, booking.getId());
                insertPax.setObject(9, pax.getId());
                insertPax.setString(10, null);
                insertPax.setString(11, null);

                insertPax.execute();
                insertPax.close();
            }

            List<Flight> flights = booking.getFlights();
            for (int i = 0; i < flights.size(); i++) {
                PreparedStatement insertFlight = connection.prepareStatement(INSERT_FLIGHT);
                
                Flight flight = flights.get(i);                
                PreparedStatement checkFlight = connection.prepareStatement(CHECK_FLIGHT_AVAILABLE);
                checkFlight.setInt(1, flight.getId());
                
                ResultSet r = checkFlight.executeQuery();
                if (!r.next()) {
                    checkFlight.close();
                    insertFlight.close();
                    return BookingStatus.FLIGHT_UNAVILABLE;
                }
                
                boolean available = r.getBoolean("available");
                checkFlight.close();
                
                if (!available) {
                    insertFlight.close();
                    return BookingStatus.FLIGHT_UNAVILABLE;
                }
                
                insertFlight.setObject(1, UUID.randomUUID());
                insertFlight.setObject(2, booking.getId());
                insertFlight.setObject(3, flight.getId());

                insertFlight.setObject(4, flight.getId());

                insertFlight.execute();
                insertFlight.close();
            }

            connection.commit();
            
            return BookingStatus.BOOK_OK;
        }
    }

    public Booking byId(UUID id) throws SQLException {
        Booking booking = database.prepareStatement(SELECT, (c, s) -> {
            s.setObject(1, id);
            s.setObject(2, id);

            ResultSet r = s.executeQuery();
            if (r.next()) {
                return new Booking(readUUID(r, "id"), readUUID(r, "userId"), r.getString("code"));
            }
            return null;
        });
        
        if (booking == null) {
            return null;
        }

        List<Flight> flights = database.prepareStatement(SELECT_FLIGHT, (c, s) -> {
            s.setObject(1, booking.getId());

            ModelMapper<List<Flight>> listMapper = Mapping.listOf(FlightRepository.MAPPER);
            return listMapper.map(s.executeQuery());
        });
        booking.setFlights(flights);

        List<Passenger> passengers = database.prepareStatement(SELECT_PASSENGER, (c, s) -> {
            s.setObject(1, booking.getId());

            ModelMapper<List<Passenger>> listMapper = Mapping.listOf(PASSENGER_MAPPER);
            return listMapper.map(s.executeQuery());
        });
        booking.setPassengers(passengers);

        return booking;
    }

    public UUID idByCode(String code) throws SQLException {
        return database.prepareStatement(ID_BY_CODE, (c, s) -> {
            s.setString(1, code);

            ResultSet r = s.executeQuery();
            if (r.next()) {
                return readUUID(r, "id");
            }
            
            return null;
        });
    }

    public BookingStatus setSeat(String bookCode, int passenger, String seatFrom, String seatBack) throws SQLException {
        UUID bookId = idByCode(bookCode);
        
        UUID seatId = database.prepareStatement(GET_SEAT_ID, (c, s) -> {
            s.setInt(1, passenger);
            s.setObject(2, bookId);
            
            ResultSet r = s.executeQuery();
            if (r.next()) {
                return readUUID(r, "id");
            }
            
            return null;
        });
        
        if (seatId == null) {
            return BookingStatus.SEAT_NOT_FOUND;
        }

        if (seatFrom != null) {
            boolean available = database.prepareStatement(CHECK_SEAT_FROM, (c, s) -> {
                s.setString(1, seatFrom);
                
                return !s.executeQuery().next();
            });
            
            if (!available) {
                return BookingStatus.SEAT_FROM_OCCUPIED;
            }
            
            database.prepareStatement(UPDATE_SEAT_FROM, (c, s) -> {
                s.setString(1, seatFrom);
                s.setObject(2, seatId);
    
                return s.execute();
            });
        }

        if (seatBack != null) {
            boolean available = database.prepareStatement(CHECK_SEAT_BACK, (c, s) -> {
                s.setString(1, seatBack);
                
                return !s.executeQuery().next();
            });
            
            if (!available) {
                return BookingStatus.SEAT_BACK_OCCUPIED;
            }
            
            database.prepareStatement(UPDATE_SEAT_BACK, (c, s) -> {
                s.setString(1, seatBack);
                s.setObject(2, seatId);
    
                return s.execute();
            });
        }
        
        return BookingStatus.BOOK_OK;
    }

    public List<Booking> list(UUID user) throws SQLException {
        List<UUID> ids = database.prepareStatement(LIST_ALL, (c, s) -> {
            s.setObject(1, user);
            return Mapping.listOf((r) -> readUUID(r, "booking")).map(s.executeQuery());
        });

        ArrayList<Booking> list = new ArrayList<>();
        for (UUID id : ids) {
            list.add(byId(id));
        }

        return list;
    }
}
