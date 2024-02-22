package com.telerikacademy.web.carpooling.models;
public class TravelApplicationDto {
    private int travelId;
    private String passengerUsername;
    private String status;

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
}
