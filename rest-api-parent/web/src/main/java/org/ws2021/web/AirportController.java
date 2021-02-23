package org.ws2021.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ws2021.data.models.Airport;
import org.ws2021.models.WebList;
import org.ws2021.models.holder.DataHolder;
import org.ws2021.models.holder.ErrorHolder;
import org.ws2021.models.holder.Holder;
import org.ws2021.service.AirportService;

@RestController
public class AirportController {
    private AirportService airports;
    
    public AirportController(AirportService airports) {
        this.airports = airports;
    }

    @GetMapping("/api/airport")
    public ResponseEntity<Holder> list(@RequestParam String query) {
        if (!query.matches("[a-zA-Zа-яА-Я]*")) {
            ErrorHolder error = new ErrorHolder(422, "Validation error");
            error.addError("query", "Allowed only simple query (only IATA or name)");
            return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        List<Airport> list = airports.list(query);
        if (list == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        DataHolder<WebList<Airport>> holder = new DataHolder<>(new WebList<>(list));
        return new ResponseEntity<>(holder, HttpStatus.OK); 
    }
}
