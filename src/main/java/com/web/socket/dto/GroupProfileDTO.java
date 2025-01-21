package com.web.socket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GroupProfileDTO {
    private String avatar;
    private String name;
    private String roomType;
}
