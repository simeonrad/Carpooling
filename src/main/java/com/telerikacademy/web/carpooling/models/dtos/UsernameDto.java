package com.telerikacademy.web.carpooling.models.dtos;

import jakarta.validation.constraints.NotEmpty;

public class UsernameDto {
    @NotEmpty(message = "Username should not be empty")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
