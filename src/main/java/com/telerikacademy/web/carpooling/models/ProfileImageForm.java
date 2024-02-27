package com.telerikacademy.web.carpooling.models;

import org.springframework.web.multipart.MultipartFile;

public class ProfileImageForm {
    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}