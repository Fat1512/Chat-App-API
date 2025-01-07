package com.web.socket.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class MessageStatusDTO {
    private String chatRoomId;
    private String messageId;

    //2 beneath list support for group chat
    private List<String> undeliveredMembers;
    private List<String> unreadMembers;
    private Boolean readStatus;
    private Boolean deliveredStatus;
    private String senderId;
}
