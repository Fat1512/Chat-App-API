package com.web.socket.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class OAuthLoginRequest {
    private String email;
    private String name;
    private String photo;
}
