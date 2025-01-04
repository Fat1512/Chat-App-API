package com.web.socket.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserAuthResponse {
    private String id;
    private Boolean onlineStatus;
    private String name;
    private String username;
    private String bio;
    private String avt;
    private Boolean isAuthenticated;
    private TokenResponse tokenResponse;
}
