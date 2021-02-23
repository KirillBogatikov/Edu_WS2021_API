package org.ws2021.models.holder;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorHolder implements Holder {
    public static class Error {
        private int code;
        private String message;
        private Map<String, String[]> errors;
        
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Map<String, String[]> getErrors() {
            return errors;
        }
        
        public void setErrors(Map<String, String[]> errors) {
            this.errors = errors;
        }                
    }

    @JsonProperty("error")
    private Error error;
    
    public ErrorHolder(int code, String message) {
        error = new Error();
        error.setCode(code);
        error.setMessage(message);
        error.setErrors(new HashMap<>());
    }
    
    public void addError(String key, String... errors) {
        error.getErrors().put(key, errors);
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
    
    public boolean isEmpty() {
        return error.getErrors().isEmpty();
    }
}
