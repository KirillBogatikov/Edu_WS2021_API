package org.ws2021.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.ws2021.data.models.Booking;
import org.ws2021.data.models.BookingStatus;
import org.ws2021.data.models.Flight;
import org.ws2021.data.models.Passenger;
import org.ws2021.models.booking.BookRequest;
import org.ws2021.models.booking.BookResponse;
import org.ws2021.models.booking.WebPassenger;
import org.ws2021.models.booking.WebSeat;
import org.ws2021.models.holder.DataHolder;
import org.ws2021.models.holder.ErrorHolder;
import org.ws2021.models.holder.Holder;
import org.ws2021.service.BookService;
import org.ws2021.service.FlightService;
import org.ws2021.util.BookCodeGenerator;
import org.ws2021.util.JwtAuth;

@RestController
public class BookingController extends EnhancedController {
    private BookService booking;
    private FlightService flights;

    public BookingController(JwtAuth jwt, BookService booking, FlightService flights) {
        super(jwt);
        this.booking = booking;
        this.flights = flights;
    }
    
    @PostMapping("/api/booking")
    public ResponseEntity<Holder> createBook(@RequestBody BookRequest request, @RequestHeader("Authorization") String token) {
        UUID userId = auth(token);
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        Flight from = flights.getById(request.getFrom().getId());
        if (from == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        Flight back = flights.getById(request.getBack().getId());
        if (back == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
       
        Booking booking = new Booking(UUID.randomUUID(), userId, BookCodeGenerator.generate());
        booking.setFlights(Arrays.asList(from, back));
        
        List<WebPassenger> webPassengers = request.getPassengers();
        ArrayList<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < webPassengers.size(); i++) {
            WebPassenger webPax = webPassengers.get(i);
            Passenger pax = new Passenger(UUID.randomUUID(), i, webPax.getFirstName(), webPax.getLastName(), webPax.getBirthDate(), webPax.getDocumentNumber());
            passengers.add(pax);
        }
        booking.setPassengers(passengers);
        
        switch(this.booking.book(booking)) {
            case FLIGHT_UNAVILABLE: {
                ErrorHolder error = new ErrorHolder(422, "Validation error");
                error.addError("flight", "One of flights has not free seats");
                return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            case BOOK_OK: {
                DataHolder<BookResponse> holder = new DataHolder<>(new BookResponse(booking.getCode()));
                return new ResponseEntity<>(holder, HttpStatus.CREATED);
            }
            default:
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
        
    @GetMapping("/api/booking/{code}")
    public ResponseEntity<Holder> getBook(@PathVariable("code") String code, @RequestHeader("Authorization") String token) {
        UUID id = auth(token);
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    
        Booking book = booking.getByCode(code);
        if (book == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (book.getUserId().equals(id)) {
            DataHolder<Booking> holder = new DataHolder<>(book);
            return new ResponseEntity<>(holder, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    
    @PatchMapping("/api/booking/{code}/seat")
    public ResponseEntity<Holder> patchSeat(@PathVariable("code") String code, @RequestBody WebSeat seat, @RequestHeader("Authorization") String token) {
        UUID id = auth(token);
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        Booking book = booking.getByCode(code);
        if (!book.getUserId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    
        BookingStatus status;
        if (seat.getType().equalsIgnoreCase("from")) {
            status = booking.setSeatFrom(code, seat.getPassenger(), seat.getSeat());
        } else {
            status = booking.setSeatBack(code, seat.getPassenger(), seat.getSeat());
        }
        
        switch(status) {
            case SEAT_FROM_OCCUPIED: { 
                ErrorHolder holder = new ErrorHolder(422, "Validation errors");
                holder.addError("Seat from", "already occupied");
                return new ResponseEntity<>(holder, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            case SEAT_BACK_OCCUPIED: {
                ErrorHolder holder = new ErrorHolder(422, "Validation errors"); 
                holder.addError("Seat from", "already occupied");
                return new ResponseEntity<>(holder, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            case SEAT_NOT_FOUND: {
                ErrorHolder holder = new ErrorHolder(422, "Validation errors"); 
                holder.addError("Passenger", "not found");
                return new ResponseEntity<>(holder, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            case BOOK_OK: {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            default:
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
