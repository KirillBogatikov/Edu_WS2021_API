package org.ws2021.models.booking;

import java.util.List;

import org.ws2021.data.models.Flight;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookRequest {
    @JsonProperty("flight_from")
    private Flight from;
    
    @JsonProperty("flight_back")
    private Flight bask;
    
    private List<WebPassenger> passengers;

    public Flight getFrom() {
        return from;
    }

    public void setFrom(Flight from) {
        this.from = from;
    }

    public Flight getBack() {
        return bask;
    }

    public void setBask(Flight bask) {
        this.bask = bask;
    }

    public List<WebPassenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<WebPassenger> passengers) {
        this.passengers = passengers;
    }
}
