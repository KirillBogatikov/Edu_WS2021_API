package org.ws2021.data.models;

public class Airport {
    private final String iata;
    private String name;

    public Airport(String iata, String name) {
        super();
        this.iata = iata;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIata() {
        return iata;
    }

}
