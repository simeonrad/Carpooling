package com.telerikacademy.web.carpooling.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class UserProfileDto {

    @NotBlank(message = "First name is required")
    @Size(min = 4, max = 32, message = "First name must be between 4 and 32 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 characters")
    private String lastName;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
