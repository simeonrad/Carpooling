package com.telerikacademy.web.carpooling.models.dtos;

import java.time.LocalDateTime;

public class TravelDto {

    private String driverUsername;
    private String startPoint;
    private String endPoint;
    private LocalDateTime departureTime;
    private int freeSpots;
    private int distanceKm;
    private int durationMinutes;
    private String status;
    private String comment;
    private Integer carId;

    public TravelDto() {
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }


    public String getDriverUsername() {
        return driverUsername;
    }

    public void setDriverUsername(String driverName) {
        this.driverUsername = driverName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getFreeSpots() {
        return freeSpots;
    }

    public void setFreeSpots(int freeSpots) {
        this.freeSpots = freeSpots;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(int distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        return "TravelDto{" +
                ", driverUserName='" + driverUsername + '\'' +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", departureTime=" + departureTime +
                ", freeSpots=" + freeSpots +
                ", status='" + status + '\'' +
                '}';
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
