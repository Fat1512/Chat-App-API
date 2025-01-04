package com.web.socket.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String username, name, password, confirmedPassword;
}
