package com.web.socket.dto.response;


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
    private List<String> undeliveredMembers;
    private List<String> unreadMembers;
}
