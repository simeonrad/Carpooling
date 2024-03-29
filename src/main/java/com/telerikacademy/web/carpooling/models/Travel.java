package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToOne
    @JoinColumn(name = "start_point", nullable = false)
    private Location startPoint;
    @OneToOne
    @JoinColumn(name = "end_point", nullable = false)
    private Location endPoint;
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

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @OneToMany(mappedBy = "travel", fetch = FetchType.LAZY)
    private Set<TravelApplication> applications = new HashSet<>();

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

    public Location getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Location startPoint) {
        this.startPoint = startPoint;
    }

    public Location getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Location endPoint) {
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

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Set<TravelApplication> getApplications() {
        return applications;
    }

    public void setApplications(Set<TravelApplication> applications) {
        this.applications = applications;
    }
}
