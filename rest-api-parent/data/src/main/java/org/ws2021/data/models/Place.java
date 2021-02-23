package org.ws2021.data.models;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Place {
    private UUID id;
    private String city;
    
    @JsonProperty("airport")
    private String airportName;
    private String iata;

    @JsonIgnore
    private Date dateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm")
    private Date time;
    
    public Place(UUID id, String city, String airportName, String iata, Date dateTime) {
        this.id = id;
        this.city = city;
        this.airportName = airportName;
        this.iata = iata;
        this.dateTime = dateTime;
        
        this.date = dateTime;
        this.time = dateTime;
    }  
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        this.date = dateTime;
        this.time = dateTime;
    }
   
}
