package com.web.socket.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
public class UserProfileDTO {
    private String id;
    private Boolean onlineStatus;
    private String name;
    private String username;
    private String bio;
    private String avt;
    private List<String> chatRoomIds;
}
