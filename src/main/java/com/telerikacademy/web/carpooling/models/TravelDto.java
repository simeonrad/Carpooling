package com.telerikacademy.web.carpooling.models;

import java.time.LocalDateTime;

public class TravelDto {
    private int id;
    private String username;
    private String driverName;
    private String startPoint;
    private String endPoint;
    private LocalDateTime departureTime;
    private int freeSpots;
    private int statusId;
    private String status;

    public TravelDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TravelDto{" +
                "id=" + id +
                ", username=" + username +
                ", driverName='" + driverName + '\'' +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", departureTime=" + departureTime +
                ", freeSpots=" + freeSpots +
                ", statusId=" + statusId +
                ", status='" + status + '\'' +
                '}';
    }
}
