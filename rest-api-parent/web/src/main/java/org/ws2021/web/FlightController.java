package org.ws2021.web;

import static org.ws2021.util.DateUtil.now;
import static org.ws2021.util.DateUtil.plusHours;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ws2021.data.models.Flight;
import org.ws2021.models.FlightList;
import org.ws2021.models.holder.DataHolder;
import org.ws2021.models.holder.ErrorHolder;
import org.ws2021.models.holder.Holder;
import org.ws2021.service.FlightService;

@RestController
public class FlightController {
    private FlightService flights;

    public FlightController(FlightService flights) {
        this.flights = flights;
    }

    @GetMapping("/api/flight")
    public ResponseEntity<Holder> getFlights(
            @RequestParam("from") String fromIata, @RequestParam("to") String toIata,
            @RequestParam("date1") @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodStart, 
            @RequestParam("date2") @DateTimeFormat(pattern = "yyyy-MM-dd") Date periodEnd,
            @RequestParam("passengers") int passengers) {
        
        Map<String, String[]> errors = validateFlightQuery(fromIata, toIata, periodStart, periodEnd, passengers);
        if (!errors.isEmpty()) {
            ErrorHolder holder = new ErrorHolder(422, "Validation error");
            holder.getError().setErrors(errors);
            return new ResponseEntity<>(holder, HttpStatus.UNPROCESSABLE_ENTITY);
        }        

        List<Flight> to = flights.getAll(fromIata, toIata, periodStart, periodEnd, passengers);
        if (to == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        List<Flight> back = flights.getAll(toIata, fromIata, periodStart, periodEnd, passengers);
        if (back == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(new DataHolder<FlightList>(new FlightList(to, back)), HttpStatus.OK);
    }
    
    private Map<String, String[]> validateFlightQuery(String fromIata, String toIata, Date periodStart, Date periodEnd, int passengers) {
        HashMap<String, String[]> result = new HashMap<>();
        
        if (fromIata == null || !fromIata.matches("[A-ZА-Я]{3}")) {
            result.put("from", new String[]{ "required valid IATA code (3 latin characters)", "Example: VKO, LED, DME" });
        }
        if (toIata == null || !toIata.matches("[A-ZА-Я]{3}")) {
            result.put("to", new String[]{ "required valid IATA code (3 latin characters)", "Example: VKO, LED, DME" });
        }
        if (periodStart == null || periodStart.before(now())) {
            result.put("date1", new String[] { "required valid date YYYY-MM-DD", "date1 should be after now (" + now() + ")" });
        }
        if (periodEnd == null || periodEnd.before(plusHours(periodStart, 1))) {
            result.put("date2", new String[] { "required valid date YYYY-MM-DD", "date2 should be after date1" });
        }
        if (passengers < 0 || passengers > 32) {
            result.put("passengers", new String[] { "minimum - 1", "maximum - 32" });
        }
        
        return result;
    }
}
