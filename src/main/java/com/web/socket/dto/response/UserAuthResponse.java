package com.web.socket.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UserAuthResponse {
    private String id;
    private Boolean onlineStatus;
    private String name;
    private String username;
    private String bio;
    private String avt;
    private Boolean isAuthenticated;
    private TokenDTO tokenDTO;
}
