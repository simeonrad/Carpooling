package com.telerikacademy.web.carpooling.models;

import java.time.LocalDateTime;
import java.util.Optional;

public class FilterTravelOptions {
    private Optional<String> author;
    private Optional<String> startPoint;
    private Optional<String> endPoint;
    private Optional<LocalDateTime> departureTime;
    private Optional<Integer> freeSpots;
    private Optional<String> travelStatus;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilterTravelOptions(Optional<String> author, Optional<String> startPoint, Optional<String> endPoint,
                               Optional<LocalDateTime> departureTime, Optional<Integer> freeSpots, Optional<String> travelStatus,
                               Optional<String> sortBy, Optional<String> sortOrder) {
        this.author = author;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.departureTime = departureTime;
        this.freeSpots = freeSpots;
        this.travelStatus = travelStatus;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Optional<String> getAuthor() {
        return author;
    }

    public Optional<String> getStartPoint() {
        return startPoint;
    }

    public Optional<String> getEndPoint() {
        return endPoint;
    }

    public Optional<LocalDateTime> getDepartureTime() {
        return departureTime;
    }

    public Optional<Integer> getFreeSpots() {
        return freeSpots;
    }

    public Optional<String> getTravelStatus() {
        return travelStatus;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
