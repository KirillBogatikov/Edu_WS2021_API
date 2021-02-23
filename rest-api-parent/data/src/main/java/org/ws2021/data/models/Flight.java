package org.ws2021.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Flight {

    @JsonProperty("flight_id")
    private int id;
    
    @JsonProperty("flight_code")
    private String code;
    private Place from;
    private Place to;
    private double cost;
    private int availability;
    
    public Flight(int id, String code, Place from, Place to, double cost, int availability) {
        this.id = id;
        this.code = code;
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Place getFrom() {
        return from;
    }

    public void setFrom(Place from) {
        this.from = from;
    }

    public Place getTo() {
        return to;
    }

    public void setTo(Place to) {
        this.to = to;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }
}
