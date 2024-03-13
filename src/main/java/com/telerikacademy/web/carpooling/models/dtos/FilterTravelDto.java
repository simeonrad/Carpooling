package com.telerikacademy.web.carpooling.models.dtos;

import java.time.LocalDateTime;
import java.util.Optional;

public class FilterTravelDto {

    private String author;
    private String startPoint;
    private String endPoint;
    private LocalDateTime departureTime;
    private Integer freeSpots;
    private String travelStatus;
    private String sortBy;
    private String sortOrder;

    public FilterTravelDto(String author, String startPoint, String endPoint,
                           LocalDateTime departureTime, Integer freeSpots,
                           String travelStatus, String sortBy, String sortOrder) {
        this.author = author;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.departureTime = departureTime;
        this.freeSpots = freeSpots;
        this.travelStatus = travelStatus;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public Integer getFreeSpots() {
        return freeSpots;
    }

    public void setFreeSpots(Integer freeSpots) {
        this.freeSpots = freeSpots;
    }

    public String getTravelStatus() {
        return travelStatus;
    }

    public void setTravelStatus(String travelStatus) {
        this.travelStatus = travelStatus;
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
