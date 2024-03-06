package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "engine_types")
public class CarEngine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "engine_type_id")
    private int id;
    @Column(name = "type")
    private String name;

    public CarEngine() {
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
        CarEngine carEngine = (CarEngine) o;
        return Objects.equals(name, carEngine.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
