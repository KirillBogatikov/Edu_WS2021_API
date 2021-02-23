package org.ws2021.models.holder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataHolder<T> implements Holder {
    @JsonProperty("data")
    private T data;
    
    public DataHolder(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
