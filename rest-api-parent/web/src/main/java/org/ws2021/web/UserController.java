package org.ws2021.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.ws2021.data.models.Booking;
import org.ws2021.data.models.User;
import org.ws2021.models.WebList;
import org.ws2021.models.holder.DataHolder;
import org.ws2021.models.holder.Holder;
import org.ws2021.service.UserService;
import org.ws2021.util.JwtAuth;

@RestController
public class UserController extends EnhancedController {
    private UserService users;

    public UserController(JwtAuth jwt, UserService users) {
        super(jwt);
        this.users = users;
    }
    
    @GetMapping("/api/user")
    public ResponseEntity<Holder> getUser(@RequestHeader("Authorization") String token) {
        UUID id = auth(token);
        
        User user = users.get(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        DataHolder<User> holder = new DataHolder<>(user);
        return new ResponseEntity<>(holder, HttpStatus.OK);
    }
    
    @GetMapping("/api/user/booking")
    public ResponseEntity<Holder> getBookings(@RequestHeader("Authorization") String token) {
        UUID id = auth(token);
        
        List<Booking> bookings = users.listBooking(id);
        if (bookings == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        DataHolder<WebList<Booking>> holder = new DataHolder<>(new WebList<>(bookings));
        return new ResponseEntity<>(holder, HttpStatus.OK);
    }
}
