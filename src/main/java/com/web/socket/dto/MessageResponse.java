package com.web.socket.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class MessageResponse {
    private String message;
    private Object data;
    private HttpStatus status;
}
