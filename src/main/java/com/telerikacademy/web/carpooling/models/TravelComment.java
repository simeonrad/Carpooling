package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

@Entity
@Table(name = "travel_comments")
public class TravelComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int id;

    @Column(name = "comment", nullable = true) // Adjusted based on your schema; nullable = true if you want it to be optional
    private String comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", referencedColumnName = "travel_id")
    private Travel travel;

    public TravelComment() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }
}
