package com.telerikacademy.web.carpooling.models;

import java.util.Optional;

public class FilterUserOptions {
    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> firstName;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilterUserOptions(
            String username,
            String email,
            String firstName,
            String sortBy,
            String sortOrder) {
        this.email = Optional.ofNullable(email);
        this.username = Optional.ofNullable(username);
        this.firstName = Optional.ofNullable(firstName);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public Optional<String> getEmail() {
        return email;
    }
}