package com.telerikacademy.web.carpooling.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private final String apiKey = "2cff821720fc39ea6d5fb0d29d51ccbc";
    // This is a special key that allows us to use the ImgBB service to store images.

    @Override
    public String saveImage(MultipartFile file) {
        // This method takes an image file and saves it using the ImgBB service.
        RestTemplate restTemplate = new RestTemplate();
        // RestTemplate is a tool that allows us to send and receive data over the internet.
        HttpHeaders headers = new HttpHeaders();
        // HttpHeaders are like the settings that tell the ImgBB service how we're sending the data.
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // We're telling ImgBB that we're sending the data in a particular format that's used for sending files.

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // This is like a form that we fill out with the details of the image we're sending.

        body.add("image", file.getResource());
        // We're adding the actual image to the form.
        body.add("key", apiKey);
        // We're adding our special key to the form so ImgBB knows we have permission to use their service.

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // We put the form (body) and the settings (headers) into an envelope ready to be sent.

        String apiUrl = "https://api.imgbb.com/1/upload";
        // This is the address where we're sending the envelope (the URL of ImgBB's service).

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
        // We send the envelope and wait for a response. The response will be in text format (String).

        String imageUrl = extractImageUrlFromResponse(response.getBody());
        // We read the response to find the web address where our image is now stored.
        return imageUrl;
    }

    private String extractImageUrlFromResponse(String responseBody) {
        try {
            // Convert the response body (JSON) to a JSONObject
            JSONObject jsonObject = new JSONObject(responseBody);
            // We turn the response text into a structured object that we can easily read.


            // Check if the upload was successful and the status is 200
            if (jsonObject.getBoolean("success") && jsonObject.getInt("status") == 200) {
                // We check if ImgBB says the upload was a success and if the status code is 200 (which means OK).

                // Extract the image URL from the JSON response
                String imageUrl = jsonObject.getJSONObject("data").getString("url");
                // We find the part of the response that contains the web address of the image.

                return imageUrl;
                // We give back the web address of the image.
            } else {
                // If the upload wasn't successful, or the status code isn't 200...
                throw new RuntimeException("Image upload was not successful or the status code is not 200.");
                // We report an error saying something went wrong.
            }
        }  catch (JSONException e) {
            // If there was a problem reading the response text...
            throw new RuntimeException(e);
            // We report an error saying we couldn't understand the response.
        }
    }
}

