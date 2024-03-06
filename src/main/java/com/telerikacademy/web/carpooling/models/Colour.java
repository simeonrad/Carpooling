package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "car_colours")
public class Colour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "colour_id")
    private int id;
    @Column(name = "colour_name")
    private String name;

    public Colour() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Colour colour = (Colour) o;
        return Objects.equals(name, colour.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
