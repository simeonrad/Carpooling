package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "car_makes")
public class Make {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "make_id")
    private int id;
    @Column(name = "make")
    private String name;

    public Make() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Make make = (Make) o;
        return Objects.equals(name, make.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
