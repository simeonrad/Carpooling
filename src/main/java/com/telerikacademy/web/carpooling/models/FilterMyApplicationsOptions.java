package com.telerikacademy.web.carpooling.models;

import java.time.LocalDateTime;
import java.util.Optional;

public class FilterMyApplicationsOptions {
    private Optional<String> startPoint;
    private Optional<String> endPoint;
    private Optional<LocalDateTime> departureTime;
    private Optional<String> driver;
    private Optional<String> status;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilterMyApplicationsOptions(String startPoint,
                               String endPoint,
                               LocalDateTime departureTime,
                               String driver, String status,
                               String sortBy,
                               String sortOrder) {
        this.startPoint = Optional.ofNullable(startPoint);
        this.endPoint = Optional.ofNullable(endPoint);
        this.departureTime = Optional.ofNullable(departureTime);
        this.driver = Optional.ofNullable(driver);
        this.status = Optional.ofNullable(status);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Optional<String> startPoint) {
        this.startPoint = startPoint;
    }

    public Optional<String> getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Optional<String> endPoint) {
        this.endPoint = endPoint;
    }

    public Optional<LocalDateTime> getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Optional<LocalDateTime> departureTime) {
        this.departureTime = departureTime;
    }

    public Optional<String> getDriver() {
        return driver;
    }

    public void setDriver(Optional<String> driver) {
        this.driver = driver;
    }

    public Optional<String> getStatus() {
        return status;
    }

    public void setStatus(Optional<String> status) {
        this.status = status;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Optional<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Optional<String> sortOrder) {
        this.sortOrder = sortOrder;
    }
}
