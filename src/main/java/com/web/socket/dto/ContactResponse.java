package com.web.socket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ContactResponse {
    private String chatRoomId;
    private SingleProfileDTO roomInfo;
}
