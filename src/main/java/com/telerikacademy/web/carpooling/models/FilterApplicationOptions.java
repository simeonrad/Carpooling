package com.telerikacademy.web.carpooling.models;

import java.util.Optional;

public class FilterApplicationOptions {
    private Optional<String> username;
    private Optional<String> status;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilterApplicationOptions(
            String username,
            String phoneNumber,
            String sortBy,
            String sortOrder) {
        this.username = Optional.ofNullable(username);
        this.status = Optional.ofNullable(phoneNumber);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getStatus() {
        return status;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

}