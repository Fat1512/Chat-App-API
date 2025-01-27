package com.web.socket.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @PostMapping("/v1")
    public String sendMessage(@RequestPart("file") List<MultipartFile> files) {
        int x = 2;
        return "Phat dep trai";
    }
}
