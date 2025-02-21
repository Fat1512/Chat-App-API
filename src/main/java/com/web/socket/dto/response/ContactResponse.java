package com.web.socket.dto.response;

import com.web.socket.dto.SingleProfileDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ContactResponse {
    private String chatRoomId;
    private String contactId;
    private SingleProfileDTO roomInfo;
}
