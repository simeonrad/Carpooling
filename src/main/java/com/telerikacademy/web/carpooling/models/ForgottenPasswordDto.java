package com.telerikacademy.web.carpooling.models;

import jakarta.validation.constraints.NotEmpty;

public class ForgottenPasswordDto {
    @NotEmpty(message = "New password should not be empty")
    private String newPassword;

    @NotEmpty(message = "Confirm new password should not be empty")
    private String confirmNewPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
