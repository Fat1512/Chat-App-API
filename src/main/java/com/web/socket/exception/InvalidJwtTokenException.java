package com.web.socket.exception;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
