package com.web.socket.controller;

import com.web.socket.config.AmazonClient;
import com.web.socket.dto.UserProfileDTO;
import com.web.socket.dto.response.APIResponse;
import com.web.socket.service.UserService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AmazonClient amazonClient;
    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        UserProfileDTO userProfileDTO = userService.getProfile(userId);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(userProfileDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfilee()  {
        UserProfileDTO userProfileDTO = userService.getProfile();
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(userProfileDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/upload-avt")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            amazonClient.uploadFileToS3(file);
        } catch(IOException e) {
            throw new RuntimeException("Failed to upload file");
        }
        return null;
    }

}
