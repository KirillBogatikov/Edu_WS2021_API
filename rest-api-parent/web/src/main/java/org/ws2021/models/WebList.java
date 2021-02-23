package org.ws2021.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebList<T> {
    @JsonProperty("items")
    private List<T> items;

    public WebList(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> airports) {
        this.items = airports;
    }
    
}
