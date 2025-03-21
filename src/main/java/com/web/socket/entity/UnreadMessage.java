package com.web.socket.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UnreadMessage {
    private String messageId;
    private String chatRoomId;
}
