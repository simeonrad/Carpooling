package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.Column;
import org.springframework.boot.context.properties.bind.DefaultValue;

public class TravelApplicationDto {
    private int travelId;
    private String passengerUsername;
    private String status;
    private boolean wantToSmoke;
    private boolean hasBaggage;

    public TravelApplicationDto() {
    }

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
    }

    public boolean isWantToSmoke() {
        return wantToSmoke;
    }

    public void setWantToSmoke(boolean wantToSmoke) {
        this.wantToSmoke = wantToSmoke;
    }

    public boolean isHasBaggage() {
        return hasBaggage;
    }

    public void setHasBaggage(boolean hasBaggage) {
        this.hasBaggage = hasBaggage;
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
}
