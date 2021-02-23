package org.ws2021.models.booking;

public class WebSeat {
    private int passenger;
    private String seat;
    private String type;
    
    public WebSeat(int passenger, String seat, String type) {
        this.passenger = passenger;
        this.seat = seat;
        this.type = type;
    }

    public int getPassenger() {
        return passenger;
    }

    public void setPassenger(int passenger) {
        this.passenger = passenger;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
