package com.telerikacademy.web.carpooling.models.dtos;

import jakarta.persistence.Column;
import org.springframework.boot.context.properties.bind.DefaultValue;

public class TravelApplicationDto {
    private int id;
    private int travelId;
    private String passengerUsername;
    private String status;
    private Boolean smoke = false;
    private Boolean luggage = false;
    private Boolean pet = false;

    public TravelApplicationDto() {
    }

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
    }

    public String getPassengerUsername() {
        return passengerUsername;
    }

    public void setPassengerUsername(String passengerUsername) {
        this.passengerUsername = passengerUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSmoke() {
        return smoke;
    }

    public void setSmoke(Boolean smoke) {
        this.smoke = smoke;
    }

    public Boolean getLuggage() {
        return luggage;
    }

    public void setLuggage(Boolean luggage) {
        this.luggage = luggage;
    }

    public Boolean getPet() {
        return pet;
    }

    public void setPet(Boolean pet) {
        this.pet = pet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
