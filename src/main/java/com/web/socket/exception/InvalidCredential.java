package com.web.socket.exception;

public class InvalidCredential extends RuntimeException {
    public InvalidCredential(String message) {
        super(message);
    }
}
