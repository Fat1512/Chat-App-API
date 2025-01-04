package com.web.socket.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @MessageMapping("/room")
    @SendTo("/topic/room")
    public String sendMessage(@Payload String message) {
        return message;
    }
}
