package com.web.socket.controller;

import com.web.socket.dto.response.APIResponse;
import com.web.socket.exception.UnauthorizedException;
import com.web.socket.service.Impl.AmazonS3Service;
import com.web.socket.service.UserService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AmazonS3Controller {

    private final AmazonS3Service amazonS3Service;
    private final UserService userService;

    @PostMapping("/s3/upload-images")
    public ResponseEntity<APIResponse> uploadMessageImagesToS3(@RequestPart List<MultipartFile> files,
                                                              @RequestPart String chatRoomId) throws IOException {
        if(!userService.isBelongToChatRoom(chatRoomId)) {
            throw new UnauthorizedException("You are not permitted to send images to a chatroom that you do not belong to.");
        }
        List<String> imageUrls = amazonS3Service.uploadMultipleImages(files, String.format("chatroom/%s", chatRoomId));
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(imageUrls)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
