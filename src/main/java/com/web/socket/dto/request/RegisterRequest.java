package com.web.socket.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RegisterRequest {
    private String username, email, name, password, confirmedPassword;
}
