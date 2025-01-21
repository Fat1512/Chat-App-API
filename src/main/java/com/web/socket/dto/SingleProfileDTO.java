package com.web.socket.dto;


import com.web.socket.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SingleProfileDTO {
    private String id;
    private String name;
    private String username;
    private User.UserStatus status;
    private String bio;
    private String avatar;
    private String roomType;
}
