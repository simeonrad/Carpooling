package com.telerikacademy.web.carpooling.models;

import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;

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

    public FilterTravelOptions(String author, String startPoint, String endPoint,
                               LocalDateTime departureTime, Integer freeSpots, String travelStatus,
                               String sortBy, String sortOrder) {
        this.author = Optional.ofNullable(author);
        this.startPoint = Optional.ofNullable(startPoint);
        this.endPoint = Optional.ofNullable(endPoint);
        this.departureTime = Optional.ofNullable(departureTime);
        this.freeSpots = Optional.ofNullable(freeSpots);
        this.travelStatus = Optional.ofNullable(travelStatus);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public FilterTravelOptions() {
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
