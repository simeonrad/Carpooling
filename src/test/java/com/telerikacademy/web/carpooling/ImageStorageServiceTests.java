package com.telerikacademy.web.carpooling;

import com.telerikacademy.web.carpooling.services.ImageStorageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageStorageServiceTests {

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageStorageServiceImpl service;


    @Test
    public void saveImage_success() throws IOException {
        // Arrange
        // Load the real image from the test resources directory
        ClassPathResource imageResource = new ClassPathResource("dummy.jpg");
        byte[] imageBytes = StreamUtils.copyToByteArray(imageResource.getInputStream());
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "dummy.jpg",
                "image/jpeg",
                imageBytes
        );

        // Act
        String result = service.saveImage(multipartFile);

        // Assert
        assertEquals("https://i.ibb.co/QMDdgYm/dummy.webp", result);
    }


    @Test
    public void saveImage_failure() {
        // Arrange
        when(multipartFile.getResource()).thenReturn(new ByteArrayResource(new byte[]{}));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.saveImage(multipartFile));
        String expectedMessage = "400 Bad Request: \"{\"status_code\":400,\"error\":{\"message\":\"Empty upload source.\",\"code\":130},\"status_txt\":\"Bad Request\"}";
        assertTrue(exception.getMessage().contains(expectedMessage), "Actual exception message: " + exception.getMessage());
    }


}