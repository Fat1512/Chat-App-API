package com.web.socket.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Builder
public class UndeliveredMessage {
    private String messageId;
    private String chatRoomId;
}
