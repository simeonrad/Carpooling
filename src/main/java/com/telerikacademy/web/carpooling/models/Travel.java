package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "travels")
public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_id")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizer_id")
    private User driver;
    @Column(name = "start_point", nullable = false, length = 255)
    private String startPoint;
    @Column(name = "end_point", nullable = false, length = 255)
    private String endPoint;
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    @Column(name = "free_spots", nullable = false)
    private int freeSpots;
    @ManyToOne
    @JoinColumn(name = "travel_status")
    private Status status;
    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;
    @Column(name = "distance_km", nullable = false)
    private int distanceKm;
    @OneToOne(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TravelComment comment;

    public Travel() {
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(int distance_km) {
        this.distanceKm = distance_km;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TravelComment getComment() {
        return comment;
    }

    public void setComment(TravelComment comment) {
        this.comment = comment;
    }
}
