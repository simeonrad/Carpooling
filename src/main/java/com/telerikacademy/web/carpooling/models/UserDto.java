package com.telerikacademy.web.carpooling.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {
    @NotNull(message = "Username can't be empty!")
    @Size(min = 2, max = 20, message = "Username should be between 2 and 20 symbols!")
    private String username;
    @NotNull(message = "Password can't be empty!")
    @Size(min = 8, message = "Password should be at least 8 symbols!")
    private String password;

    @NotNull(message = "First name can't be empty!")
    @Size(min = 2, max = 20, message = "First name should be between 2 and 20 symbols!")
    private String firstName;

    @NotNull(message = "Last name can't be empty!")
    @Size(min = 2, max = 20, message = "Last name should be between 2 and 20 symbols!")
    private String lastName;

    @NotNull(message = "Email can't be empty!")
    private String email;

    @NotNull(message = "Telephone number can't be empty!")
    @Size(min = 10, max = 10, message = "Telephone number should be 10 symbols long!")
    private String phoneNumber;

    public UserDto() {
    }

    public UserDto(String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
