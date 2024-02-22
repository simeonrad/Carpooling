package com.telerikacademy.web.carpooling.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterDto extends LoginDto {
    @NotEmpty
    private String passwordConfirm;
    @NotEmpty
    @Size(min = 2, max = 20, message = "First name should be between 2 and 20 symbols!")
    private String firstName;
    @NotEmpty
    @Size(min = 2, max = 20, message = "Last name should be between 2 and 20 symbols!")
    private String lastName;
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 10, max = 10, message = "The phone number should be 10 symbols long!")
    private String phoneNumber;

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
