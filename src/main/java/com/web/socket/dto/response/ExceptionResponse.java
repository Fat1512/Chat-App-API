package com.web.socket.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ExceptionResponse {
    private String error;
    private int status;
    private String message;
    private Long timestamp;
}
