package com.web.socket.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExceptionResponse {
    private String error;
    private int status;
    private String message;
    private Long timestamp;
}
