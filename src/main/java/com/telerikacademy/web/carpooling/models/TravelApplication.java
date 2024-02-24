package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "applications")
public class TravelApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "passenger_id")
    private User passenger;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status")
    private Status status;

    @Column(name = "want_to_smoke")
    private boolean wantToSmoke;

    @Column(name = "has_baggage")
    private boolean hasBaggage;

    public TravelApplication() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isWantToSmoke() {
        return wantToSmoke;
    }

    public void setWantToSmoke(boolean wantToSmoke) {
        this.wantToSmoke = wantToSmoke;
    }

    public boolean isHasBaggage() {
        return hasBaggage;
    }

    public void setHasBaggage(boolean hasBaggage) {
        this.hasBaggage = hasBaggage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelApplication that = (TravelApplication) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
