package com.telerikacademy.web.carpooling.models.dtos;

public class FilterUserDto {
    private String username;
    private String email;
    private String phoneNumber;
    private String sortBy;
    private String sortOrder;

    public FilterUserDto(
            String username,
            String email,
            String phoneNumber,
            String sortBy,
            String sortOrder) {
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getEmail() {
        return email;
    }
}