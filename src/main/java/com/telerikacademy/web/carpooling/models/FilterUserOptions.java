package com.telerikacademy.web.carpooling.models;

import java.util.Optional;

public class FilterUserOptions {
    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilterUserOptions(
            String username,
            String email,
            String phoneNumber,
            String sortBy,
            String sortOrder) {
        this.email = Optional.ofNullable(email);
        this.username = Optional.ofNullable(username);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
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