package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

@Entity
@Table(name = "non_verified")
public class NonVerifiedUser {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "is_verified")
    private boolean isVerified;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
