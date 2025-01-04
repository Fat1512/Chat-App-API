package com.web.socket.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Builder
@Setter @Getter
public class APIResponse {
    private String message;
    private Object data;
    private HttpStatus status;
}
