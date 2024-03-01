package com.telerikacademy.web.carpooling.models;

import java.time.LocalDateTime;

public class FilterMyApplicationsDto {
    private String startPoint;
    private String endPoint;
    private LocalDateTime departureTime;
    private String driver;
    private String status;
    private String sortBy;
    private String sortOrder;

    public FilterMyApplicationsDto(String startPoint, String endPoint, LocalDateTime departureTime, String driver,
                                   String status, String sortBy, String sortOrder) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.departureTime = departureTime;
        this.driver = driver;
        this.status = status;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
