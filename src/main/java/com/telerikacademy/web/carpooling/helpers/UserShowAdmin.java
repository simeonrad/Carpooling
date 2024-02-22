package com.telerikacademy.web.carpooling.helpers;

import com.telerikacademy.web.carpooling.models.Role;

public class UserShowAdmin {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isBlocked;

    private Role role;

    public UserShowAdmin() {
    }

    public UserShowAdmin(String username, String firstName, String lastName, String email, boolean isBlocked, Role role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isBlocked = isBlocked;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
