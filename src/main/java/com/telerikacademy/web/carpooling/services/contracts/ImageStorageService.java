package com.telerikacademy.web.carpooling.services.contracts;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String saveImage(MultipartFile file);
}
