package com.web.socket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserUpdateDTO {
    public String id;
    public String name;
    public String bio;
    public String avt;
}
