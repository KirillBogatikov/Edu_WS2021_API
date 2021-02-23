package org.ws2021.models.booking;

public class BookResponse {
    private String code;

    public BookResponse(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
