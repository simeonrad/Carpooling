package com.telerikacademy.web.carpooling.models;


import com.telerikacademy.web.carpooling.models.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;

    public Status() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status1 = (Status) o;
        return id == status1.id && status == status1.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }
}
