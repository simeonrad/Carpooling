package com.telerikacademy.web.carpooling.models;

import jakarta.validation.constraints.NotEmpty;

public class UserPhoneNumberUpdateDto {
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
