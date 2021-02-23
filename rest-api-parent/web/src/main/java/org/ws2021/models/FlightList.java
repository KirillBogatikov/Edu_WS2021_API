package org.ws2021.models;

import java.util.List;

import org.ws2021.data.models.Flight;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlightList {
    @JsonProperty("flights_to")
    private List<Flight> to;
    
    @JsonProperty("flights_back")
    private List<Flight> back;

    public FlightList(List<Flight> to, List<Flight> back) {
        this.to = to;
        this.back = back;
    }

    public List<Flight> getTo() {
        return to;
    }

    public void setTo(List<Flight> to) {
        this.to = to;
    }

    public List<Flight> getBack() {
        return back;
    }

    public void setBack(List<Flight> back) {
        this.back = back;
    }
}
