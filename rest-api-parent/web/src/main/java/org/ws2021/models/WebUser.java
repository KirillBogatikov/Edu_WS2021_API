package org.ws2021.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebUser {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("document_number")
    private String documentNumber;
    @JsonProperty("password")
    private String password;
    
    public WebUser(String firstName, String lastName, String phone, String documentNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.documentNumber = documentNumber;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
